package com.example.twitturin.feature.tweet.presentation.detail

import com.example.twitturin.feature.tweet.presentation.TweetUi

data class DetailState(
    val tweet: TweetUi? = null,
    val replies: List<TweetUi> = emptyList(),
    val replyDraft: String = "",
    val isLoading: Boolean = false,
    val isSendingReply: Boolean = false,
)
