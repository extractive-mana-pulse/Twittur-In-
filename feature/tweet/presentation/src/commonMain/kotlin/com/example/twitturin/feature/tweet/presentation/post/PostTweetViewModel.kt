package com.example.twitturin.feature.tweet.presentation.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.tweet.domain.TweetRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostTweetViewModel(
    private val tweetRepository: TweetRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PostTweetState())
    val state = _state.asStateFlow()

    private val _events = Channel<PostTweetEvent>()
    val events = _events.receiveAsFlow()

    fun post(content: String) {
        val text = content.trim()
        if (text.isBlank() || _state.value.isPosting) return
        viewModelScope.launch {
            _state.update { it.copy(isPosting = true) }
            tweetRepository.postTweet(text)
                .onSuccess {
                    _state.update { it.copy(isPosting = false) }
                    _events.send(PostTweetEvent.Posted)
                }
                .onFailure { error ->
                    _state.update { it.copy(isPosting = false) }
                    _events.send(PostTweetEvent.ShowError(error.toUiText()))
                }
        }
    }
}
