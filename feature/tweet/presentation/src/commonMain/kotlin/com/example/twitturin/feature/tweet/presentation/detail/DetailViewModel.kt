package com.example.twitturin.feature.tweet.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.follow.domain.FollowRepository
import com.example.twitturin.feature.tweet.domain.TweetRepository
import com.example.twitturin.feature.tweet.presentation.ReplyUi
import com.example.twitturin.feature.tweet.presentation.toReplyUi
import com.example.twitturin.feature.tweet.presentation.toTweetUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val tweetRepository: TweetRepository,
    private val followRepository: FollowRepository,
    sessionSource: SessionSource,
) : ViewModel() {

    private val currentUserId: String? = sessionSource.getUserId()
    private var tweetId: String? = null

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<DetailEvent>()
    val events = _events.receiveAsFlow()

    /** Called once from the screen with the tweet id from the nav route. */
    fun load(id: String) {
        if (tweetId == id && _state.value.tweet != null) return
        tweetId = id
        refresh()
    }

    fun onAction(action: DetailAction) {
        when (action) {
            DetailAction.OnRefresh -> refresh()
            is DetailAction.OnSendReply -> sendReply(action.content)
            is DetailAction.OnReplyToReply -> _state.update { it.copy(replyTarget = action.reply, editTarget = null) }
            DetailAction.OnCancelReplyTarget -> _state.update { it.copy(replyTarget = null) }
            is DetailAction.OnStartEditReply -> _state.update { it.copy(editTarget = action.reply, replyTarget = null) }
            DetailAction.OnCancelEditReply -> _state.update { it.copy(editTarget = null) }
            is DetailAction.OnLikeReply -> toggleReplyLike(action.reply)
            is DetailAction.OnDeleteReply -> deleteReply(action.reply)
            DetailAction.OnToggleFollow -> toggleFollow()
            DetailAction.OnOpenLikes -> tweetId?.let { id ->
                viewModelScope.launch { _events.send(DetailEvent.NavigateToLikes(id)) }
            }
            DetailAction.OnLike -> toggleLike()
            DetailAction.OnDelete -> deleteTweet()
        }
    }

    private fun refresh() {
        val id = tweetId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            tweetRepository.getTweet(id)
                .onSuccess { tweet ->
                    val ui = tweet.toTweetUi(currentUserId)
                    _state.update { it.copy(tweet = ui) }
                    if (!ui.isMine && ui.authorId.isNotBlank()) loadFollowState(ui.authorId)
                }
                .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
            loadReplies(id)
            _state.update { it.copy(isLoading = false) }
        }
    }

    /** Whether the signed-in user already follows the author — drives the header button label. */
    private fun loadFollowState(authorId: String) {
        val me = currentUserId ?: return
        viewModelScope.launch {
            followRepository.getFollowers(authorId)
                .onSuccess { followers ->
                    _state.update { it.copy(isFollowingAuthor = followers.any { f -> f.id == me }) }
                }
                .onFailure { /* keep null: the follow button simply stays hidden */ }
        }
    }

    /** Optimistically flip the header follow button, then sync; revert on failure. */
    private fun toggleFollow() {
        val authorId = _state.value.tweet?.authorId?.takeIf { it.isNotBlank() } ?: return
        val current = _state.value.isFollowingAuthor ?: return
        val now = !current
        _state.update { it.copy(isFollowingAuthor = now) }
        viewModelScope.launch {
            val result = if (now) followRepository.followUser(authorId) else followRepository.unfollowUser(authorId)
            result.onFailure { error ->
                _state.update { it.copy(isFollowingAuthor = current) }
                _events.send(DetailEvent.ShowError(error.toUiText()))
            }
        }
    }

    /** Optimistically toggle the heart on the main tweet, then sync; revert on failure. */
    private fun toggleLike() {
        val id = tweetId ?: return
        val current = _state.value.tweet ?: return
        val nowLiked = !current.isLiked
        val newCount = (current.likes + if (nowLiked) 1 else -1).coerceAtLeast(0)
        _state.update { it.copy(tweet = current.copy(isLiked = nowLiked, likes = newCount)) }
        viewModelScope.launch {
            val result = if (nowLiked) {
                tweetRepository.likeTweet(id, newCount)
            } else {
                tweetRepository.unlikeTweet(id, newCount)
            }
            result.onFailure { error ->
                _state.update { it.copy(tweet = current) }
                _events.send(DetailEvent.ShowError(error.toUiText()))
            }
        }
    }

    /** Optimistically toggle the heart on a thread reply (`POST/DELETE replies/{id}/likes`). */
    private fun toggleReplyLike(reply: ReplyUi) {
        val nowLiked = !reply.isLiked
        val delta = if (nowLiked) 1 else -1
        fun apply(liked: Boolean, d: Int) = _state.update { state ->
            state.copy(
                replies = updateReplyInTree(state.replies, reply.id) {
                    it.copy(isLiked = liked, likes = (it.likes + d).coerceAtLeast(0))
                },
            )
        }
        apply(nowLiked, delta)
        viewModelScope.launch {
            val result = if (nowLiked) {
                tweetRepository.likeReply(reply.id)
            } else {
                tweetRepository.unlikeReply(reply.id)
            }
            result.onFailure { error ->
                apply(reply.isLiked, -delta)
                _events.send(DetailEvent.ShowError(error.toUiText()))
            }
        }
    }

    private fun deleteReply(reply: ReplyUi) {
        val id = tweetId ?: return
        viewModelScope.launch {
            tweetRepository.deleteReply(reply.id)
                .onSuccess { loadReplies(id) }
                .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
        }
    }

    private fun deleteTweet() {
        val id = tweetId ?: return
        viewModelScope.launch {
            tweetRepository.deleteTweet(id)
                .onSuccess { _events.send(DetailEvent.Deleted) }
                .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
        }
    }

    /**
     * Posts to the tweet, or under [DetailState.replyTarget] when one is aimed (`POST replies/{id}`),
     * or saves the [DetailState.editTarget] edit (`PUT replies/{id}`) when editing.
     */
    private fun sendReply(content: String) {
        val id = tweetId ?: return
        val text = content.trim()
        if (text.isBlank() || _state.value.isSendingReply) return
        val target = _state.value.replyTarget
        val editing = _state.value.editTarget
        viewModelScope.launch {
            _state.update { it.copy(isSendingReply = true) }
            val result = when {
                editing != null -> tweetRepository.editReply(editing.id, text)
                target != null -> tweetRepository.postReplyToReply(target.id, text)
                else -> tweetRepository.postReply(id, text)
            }
            result
                .onSuccess {
                    _state.update { it.copy(isSendingReply = false, replyTarget = null, editTarget = null) }
                    _events.send(DetailEvent.ReplySent)
                    loadReplies(id)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSendingReply = false) }
                    _events.send(DetailEvent.ShowError(error.toUiText()))
                }
        }
    }

    private suspend fun loadReplies(id: String) {
        tweetRepository.getReplies(id)
            .onSuccess { replies ->
                _state.update { it.copy(replies = replies.map { r -> r.toReplyUi(currentUserId) }) }
            }
            .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
    }

    /** Applies [transform] to the node with [id] anywhere in the reply tree. */
    private fun updateReplyInTree(
        nodes: List<ReplyUi>,
        id: String,
        transform: (ReplyUi) -> ReplyUi,
    ): List<ReplyUi> = nodes.map { node ->
        val updated = if (node.id == id) transform(node) else node
        updated.copy(replies = updateReplyInTree(updated.replies, id, transform))
    }
}
