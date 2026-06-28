package com.example.twitturin.feature.profile.presentation

import com.example.twitturin.core.presentation.UiText

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
    data object AccountDeleted : ProfileEvent
    data object Saved : ProfileEvent
    data class ShowError(val message: UiText) : ProfileEvent
}
