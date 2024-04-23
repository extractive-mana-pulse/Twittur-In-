package com.example.twitturin.auth.presentation.login.sealed

import com.example.twitturin.auth.domain.model.AuthUser

sealed class SignInUiEvent {

    data object OnLoginPressed: SignInUiEvent()

    data object OnKindPressed: SignInUiEvent()

    data object StateNoting : SignInUiEvent()
}