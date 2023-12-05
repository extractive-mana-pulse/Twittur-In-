package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.users.User

sealed class SignInResult {
    data class Success(val response: User) : SignInResult()
    data class Error(val message: String) : SignInResult()
}