package com.example.twitturin.feature.auth.presentation.registration

import com.example.twitturin.core.presentation.UiText

data class RegistrationState(
    val isLoading: Boolean = false,
)

sealed interface RegistrationEvent {
    data object Registered : RegistrationEvent
    data class ShowError(val message: UiText) : RegistrationEvent
}
