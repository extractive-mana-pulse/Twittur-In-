package com.example.twitturin.auth.presentation.stayIn.sealed

sealed class StayInUiEvent {
    data object OnSavePressed: StayInUiEvent()
    data object OnNotSavePressed: StayInUiEvent()
}