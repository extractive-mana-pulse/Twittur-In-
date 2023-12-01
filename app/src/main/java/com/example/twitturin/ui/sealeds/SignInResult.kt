package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.users.UsersItem

sealed class SignInResult {
    data class Success(val response: UsersItem) : SignInResult()
    data class Error(val message: String) : SignInResult()
}