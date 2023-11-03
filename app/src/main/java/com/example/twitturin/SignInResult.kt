package com.example.twitturin

import com.example.twitturin.presentation.data.SignIn

sealed class SignInResult {
    data class Success(val response: SignIn) : SignInResult()
    data class Error(val message: String) : SignInResult()
}