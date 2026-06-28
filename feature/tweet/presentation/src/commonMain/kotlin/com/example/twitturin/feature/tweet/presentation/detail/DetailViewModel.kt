package com.example.twitturin.feature.tweet.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.tweet.domain.TweetRepository
import com.example.twitturin.feature.tweet.presentation.toTweetUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val tweetRepository: TweetRepository,
) : ViewModel() {

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
            is DetailAction.OnReplyClick -> viewModelScope.launch {
                _events.send(DetailEvent.NavigateToTweet(action.tweetId))
            }
            DetailAction.OnOpenLikes -> tweetId?.let { id ->
                viewModelScope.launch { _events.send(DetailEvent.NavigateToLikes(id)) }
            }
        }
    }

    private fun refresh() {
        val id = tweetId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            tweetRepository.getTweet(id)
                .onSuccess { tweet -> _state.update { it.copy(tweet = tweet.toTweetUi(null)) } }
                .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
            loadReplies(id)
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendReply(content: String) {
        val id = tweetId ?: return
        val text = content.trim()
        if (text.isBlank() || _state.value.isSendingReply) return
        viewModelScope.launch {
            _state.update { it.copy(isSendingReply = true) }
            tweetRepository.postReply(id, text)
                .onSuccess {
                    _state.update { it.copy(isSendingReply = false, replyDraft = "") }
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
            .onSuccess { replies -> _state.update { it.copy(replies = replies.map { r -> r.toTweetUi(null) }) } }
            .onFailure { error -> _events.send(DetailEvent.ShowError(error.toUiText())) }
    }
}
