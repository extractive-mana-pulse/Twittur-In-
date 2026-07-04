package com.example.twitturin.feature.tweet.presentation.detail

import com.example.twitturin.feature.tweet.presentation.ReplyUi
import com.example.twitturin.feature.tweet.presentation.TweetUi

data class DetailState(
    val tweet: TweetUi? = null,
    val replies: List<ReplyUi> = emptyList(),
    val replyDraft: String = "",
    /** When set, Send posts a nested reply under this node instead of a top-level reply. */
    val replyTarget: ReplyUi? = null,
    val isLoading: Boolean = false,
    val isSendingReply: Boolean = false,
)
