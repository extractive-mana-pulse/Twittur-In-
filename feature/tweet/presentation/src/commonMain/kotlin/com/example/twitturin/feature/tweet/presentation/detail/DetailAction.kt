package com.example.twitturin.feature.tweet.presentation.detail

import com.example.twitturin.feature.tweet.presentation.ReplyUi

sealed interface DetailAction {
    data object OnRefresh : DetailAction
    data class OnReplyChange(val text: String) : DetailAction
    data class OnSendReply(val content: String) : DetailAction

    /** "Reply" tapped on a thread row — aim the composer at that reply. */
    data class OnReplyToReply(val reply: ReplyUi) : DetailAction

    /** Dismiss the "Replying to @user" target and go back to replying to the tweet itself. */
    data object OnCancelReplyTarget : DetailAction

    data object OnOpenLikes : DetailAction
    data object OnLike : DetailAction
    data object OnDelete : DetailAction
}
