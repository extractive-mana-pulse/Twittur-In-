package com.example.twitturin.feature.tweet.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.tweet.domain.TweetRepository
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
            is DetailAction.OnReplyChange -> _state.update { it.copy(replyDraft = action.text) }
            is DetailAction.OnSendReply -> sendReply(action.content)
            is DetailAction.OnReplyToReply -> _state.update { it.copy(replyTarget = action.reply) }
            DetailAction.OnCancelReplyTarget -> _state.update { it.copy(replyTarget = null) }
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
                .onSuccess { tweet -> _state.update { it.copy(tweet = tweet.toTweetUi(currentUserId)) } }
                .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
            loadReplies(id)
            _state.update { it.copy(isLoading = false) }
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

    private fun deleteTweet() {
        val id = tweetId ?: return
        viewModelScope.launch {
            tweetRepository.deleteTweet(id)
                .onSuccess { _events.send(DetailEvent.Deleted) }
                .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
        }
    }

    /** Posts to the tweet, or under [DetailState.replyTarget] when one is aimed (`POST replies/{id}`). */
    private fun sendReply(content: String) {
        val id = tweetId ?: return
        val text = content.trim()
        if (text.isBlank() || _state.value.isSendingReply) return
        val target = _state.value.replyTarget
        viewModelScope.launch {
            _state.update { it.copy(isSendingReply = true) }
            val result = if (target != null) {
                tweetRepository.postReplyToReply(target.id, text)
            } else {
                tweetRepository.postReply(id, text)
            }
            result
                .onSuccess {
                    _state.update { it.copy(isSendingReply = false, replyDraft = "", replyTarget = null) }
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
            .onSuccess { replies -> _state.update { it.copy(replies = replies.map { r -> r.toReplyUi() }) } }
            .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
    }
}
