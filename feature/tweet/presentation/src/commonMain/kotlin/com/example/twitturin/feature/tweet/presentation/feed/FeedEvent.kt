package com.example.twitturin.feature.tweet.presentation.feed

import com.example.twitturin.core.presentation.UiText

sealed interface FeedEvent {
    data class NavigateToTweet(val tweetId: String) : FeedEvent
    data class ShowError(val message: UiText) : FeedEvent
}
