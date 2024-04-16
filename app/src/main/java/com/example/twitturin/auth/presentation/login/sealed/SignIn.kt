package com.example.twitturin.auth.presentation.login.sealed

import com.example.twitturin.auth.domain.model.AuthUser

sealed class SignIn {
    data class Success(val response: AuthUser) : SignIn()
    data class Error(val message: String) : SignIn()
}