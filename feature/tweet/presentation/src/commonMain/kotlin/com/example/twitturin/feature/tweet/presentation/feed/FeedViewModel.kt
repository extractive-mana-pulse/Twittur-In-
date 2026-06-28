package com.example.twitturin.feature.tweet.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.domain.auth.SessionSource
import com.example.twitturin.core.domain.util.onFailure
import com.example.twitturin.core.domain.util.onSuccess
import com.example.twitturin.core.presentation.toUiText
import com.example.twitturin.feature.tweet.domain.TweetRepository
import com.example.twitturin.feature.tweet.presentation.toTweetUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val tweetRepository: TweetRepository,
    sessionSource: SessionSource,
) : ViewModel() {

    private val currentUserId: String? = sessionSource.getUserId()

    private val _state = MutableStateFlow(FeedState())
    val state = _state
        .onStart { loadFeed() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = FeedState(),
        )

    private val _events = Channel<FeedEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: FeedAction) {
        when (action) {
            FeedAction.OnRefresh -> loadFeed()
            is FeedAction.OnTweetClick -> viewModelScope.launch {
                _events.send(FeedEvent.NavigateToTweet(action.tweetId))
            }
            is FeedAction.OnDeleteTweet -> deleteTweet(action.tweetId)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            tweetRepository.getFeed()
                .onSuccess { tweets ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            tweets = tweets.map { tweet -> tweet.toTweetUi(currentUserId) },
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(FeedEvent.ShowError(error.toUiText()))
                }
        }
    }

    private fun deleteTweet(tweetId: String) {
        viewModelScope.launch {
            tweetRepository.deleteTweet(tweetId)
                .onSuccess {
                    // Drop it locally so the list updates without a full refetch.
                    _state.update { state -> state.copy(tweets = state.tweets.filterNot { it.id == tweetId }) }
                }
                .onFailure { error -> _events.send(FeedEvent.ShowError(error.toUiText())) }
        }
    }
}
