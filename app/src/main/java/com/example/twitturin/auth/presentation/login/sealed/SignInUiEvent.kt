package com.example.twitturin.auth.presentation.login.sealed

sealed class SignInUiEvent {

    data object OnLoginPressed: SignInUiEvent()
    data object OnKindPressed: SignInUiEvent()
    data object StateNoting : SignInUiEvent()
}