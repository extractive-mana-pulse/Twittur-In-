package com.example.twitturin.auth.sealed

import com.example.twitturin.model.data.users.User

sealed class SignInResult {
    data class Success(val response: User) : SignInResult()
    data class Error(val message: String) : SignInResult()
}