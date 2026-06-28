package com.example.twitturin.feature.tweet.presentation.feed

sealed interface FeedAction {
    data object OnRefresh : FeedAction
    data class OnTweetClick(val tweetId: String) : FeedAction
    data class OnDeleteTweet(val tweetId: String) : FeedAction
}
