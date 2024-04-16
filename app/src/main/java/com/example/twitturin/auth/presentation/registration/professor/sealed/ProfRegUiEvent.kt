package com.example.twitturin.auth.presentation.registration.professor.sealed

sealed class ProfRegUiEvent {
    data object OnAuthPressed: ProfRegUiEvent()
}