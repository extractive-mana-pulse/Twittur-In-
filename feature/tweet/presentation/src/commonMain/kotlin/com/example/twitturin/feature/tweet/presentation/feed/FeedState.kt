package com.example.twitturin.feature.tweet.presentation.feed

import com.example.twitturin.feature.tweet.presentation.TweetUi

data class FeedState(
    val tweets: List<TweetUi> = emptyList(),
    val isLoading: Boolean = false,
)
