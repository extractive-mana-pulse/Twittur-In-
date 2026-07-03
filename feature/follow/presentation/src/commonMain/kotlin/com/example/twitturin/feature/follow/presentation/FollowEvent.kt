package com.example.twitturin.feature.follow.presentation

import com.example.twitturin.core.presentation.UiText

sealed interface FollowEvent {
    data class NavigateToProfile(val userId: String) : FollowEvent
    data class ShowMessage(val message: UiText) : FollowEvent
}
