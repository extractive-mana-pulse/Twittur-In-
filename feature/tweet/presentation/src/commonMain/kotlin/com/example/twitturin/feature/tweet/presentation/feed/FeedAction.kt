package com.example.twitturin.feature.tweet.presentation.feed

sealed interface FeedAction {
    data object OnRefresh : FeedAction
    data class OnTweetClick(val tweetId: String) : FeedAction
    data class OnReplyClick(val tweetId: String) : FeedAction
    data class OnLikeClick(val tweetId: String) : FeedAction
}
