package com.example.twitturin.feature.tweet.presentation.detail

import com.example.twitturin.core.presentation.UiText

sealed interface DetailEvent {
    data class NavigateToLikes(val tweetId: String) : DetailEvent
    data object Deleted : DetailEvent

    /** A reply was accepted by the server — the screen clears its rich-text composer. */
    data object ReplySent : DetailEvent

    data class ShowError(val message: UiText) : DetailEvent
}
