package com.example.twitturin.feature.notification.presentation

sealed interface NotificationAction {
    data object OnRefresh : NotificationAction
    data object OnPatchNoteClick : NotificationAction
}
