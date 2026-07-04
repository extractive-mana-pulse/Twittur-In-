package com.example.twitturin.feature.tweet.presentation.post

import com.example.twitturin.core.presentation.UiText

sealed interface PostTweetEvent {
    data object Posted : PostTweetEvent
    data class ShowError(val message: UiText) : PostTweetEvent
}
