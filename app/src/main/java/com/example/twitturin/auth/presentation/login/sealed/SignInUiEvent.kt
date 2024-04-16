package com.example.twitturin.auth.presentation.login.sealed

sealed class SignInUiEvent {
    data object OnLoginPressed: SignInUiEvent()
}