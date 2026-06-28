package com.example.twitturin.feature.notification.presentation

import com.example.twitturin.core.presentation.UiText

sealed interface NotificationEvent {
    data object NavigateToPatchNote : NotificationEvent
    data class ShowError(val message: UiText) : NotificationEvent
}
