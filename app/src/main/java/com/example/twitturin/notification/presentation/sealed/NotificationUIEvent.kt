package com.example.twitturin.notification.presentation.sealed

sealed class NotificationUIEvent {

    data object OnDownloadPressed: NotificationUIEvent()

    data object OnItemPressed: NotificationUIEvent()

}