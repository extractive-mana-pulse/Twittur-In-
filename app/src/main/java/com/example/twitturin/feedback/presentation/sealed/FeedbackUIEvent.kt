package com.example.twitturin.feedback.presentation.sealed

sealed class FeedbackUIEvent {

    data object OnBackPressed : FeedbackUIEvent()

    data object OnSendPressed : FeedbackUIEvent()
}