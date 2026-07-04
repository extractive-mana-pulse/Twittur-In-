package com.example.twitturin.feature.tweet.presentation.feed

import com.example.twitturin.core.presentation.UiText

sealed interface FeedEvent {
    /** Open the tweet detail; [focusReply] requests the reply field + keyboard on arrival. */
    data class NavigateToTweet(val tweetId: String, val focusReply: Boolean = false) : FeedEvent
    data class ShowError(val message: UiText) : FeedEvent
}
