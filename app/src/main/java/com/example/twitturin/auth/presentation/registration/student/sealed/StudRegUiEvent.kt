package com.example.twitturin.auth.presentation.registration.student.sealed

sealed class StudRegUiEvent {
    data object OnRegPressed: StudRegUiEvent()
}