package com.example.twitturin.feature.tweet.presentation.detail

sealed interface DetailAction {
    data object OnRefresh : DetailAction
    data class OnReplyChange(val text: String) : DetailAction
    data class OnSendReply(val content: String) : DetailAction
    data class OnReplyClick(val tweetId: String) : DetailAction
    data object OnOpenLikes : DetailAction
    data object OnLike : DetailAction
    data object OnDelete : DetailAction
}
