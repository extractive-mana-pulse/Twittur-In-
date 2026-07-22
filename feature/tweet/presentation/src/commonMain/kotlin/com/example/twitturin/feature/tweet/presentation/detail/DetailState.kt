package com.example.twitturin.feature.tweet.presentation.detail

import com.example.twitturin.feature.tweet.presentation.ReplyUi
import com.example.twitturin.feature.tweet.presentation.TweetUi

data class DetailState(
    val tweet: TweetUi? = null,
    val replies: List<ReplyUi> = emptyList(),
    /** When set, Send posts a nested reply under this node instead of a top-level reply. */
    val replyTarget: ReplyUi? = null,
    /** When set, Send saves an edit of this reply (`PUT replies/{id}`) instead of posting. */
    val editTarget: ReplyUi? = null,
    /** Whether the signed-in user follows the tweet's author; null while unknown / own tweet. */
    val isFollowingAuthor: Boolean? = null,
    val isLoading: Boolean = false,
    val isSendingReply: Boolean = false,
)
