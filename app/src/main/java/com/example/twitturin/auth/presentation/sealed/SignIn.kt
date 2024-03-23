package com.example.twitturin.auth.presentation.sealed

import com.example.twitturin.auth.presentation.model.data.User

sealed class SignIn {
    data class Success(val response: User) : SignIn()
    data class Error(val message: String) : SignIn()
}