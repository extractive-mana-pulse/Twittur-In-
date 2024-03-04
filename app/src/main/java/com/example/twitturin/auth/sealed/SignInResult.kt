package com.example.twitturin.auth.sealed

import com.example.twitturin.auth.model.data.User

sealed class SignInResult {
    data class Success(val response: User) : SignInResult()
    data class Error(val message: String) : SignInResult()
}