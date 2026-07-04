package com.example.twitturin.feature.auth.presentation.login

import com.example.twitturin.core.presentation.UiText

sealed interface LoginEvent {
    data object LoggedIn : LoginEvent
    data class ShowError(val message: UiText) : LoginEvent
}
