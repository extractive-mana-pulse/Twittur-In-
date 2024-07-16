package com.example.twitturin.auth.presentation.registration.student.sealed

sealed class StudentRegistrationUiEvent {

    data object OnRegisterPressed : StudentRegistrationUiEvent()

    data object OnBackPressed : StudentRegistrationUiEvent()
}