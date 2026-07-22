package com.example.twitturin.feature.tweet.presentation.detail

import com.example.twitturin.feature.tweet.presentation.ReplyUi

sealed interface DetailAction {
    data object OnRefresh : DetailAction

    /** [content] is wire-format rich text (the reply composer's encoded document). */
    data class OnSendReply(val content: String) : DetailAction

    /** "Reply" tapped on a thread row — aim the composer at that reply. */
    data class OnReplyToReply(val reply: ReplyUi) : DetailAction

    /** Dismiss the "Replying to @user" target and go back to replying to the tweet itself. */
    data object OnCancelReplyTarget : DetailAction

    /** Start editing one of the signed-in user's replies (composer prefills with its content). */
    data class OnStartEditReply(val reply: ReplyUi) : DetailAction

    /** Dismiss the "Editing reply" state without saving. */
    data object OnCancelEditReply : DetailAction

    /** Toggle the heart on a thread reply. */
    data class OnLikeReply(val reply: ReplyUi) : DetailAction

    /** Delete one of the signed-in user's replies (already confirmed by the screen). */
    data class OnDeleteReply(val reply: ReplyUi) : DetailAction

    /** Follow/unfollow the tweet's author from the detail header. */
    data object OnToggleFollow : DetailAction

    data object OnOpenLikes : DetailAction
    data object OnLike : DetailAction
    data object OnDelete : DetailAction
}
