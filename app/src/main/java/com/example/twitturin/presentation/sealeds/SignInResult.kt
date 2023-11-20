package com.example.twitturin.presentation.sealeds

import com.example.twitturin.presentation.data.users.UsersItem

sealed class SignInResult {
    data class Success(val response: UsersItem) : SignInResult()
    data class Error(val message: String) : SignInResult()
}