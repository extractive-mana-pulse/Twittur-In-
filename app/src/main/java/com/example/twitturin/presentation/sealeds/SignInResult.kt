package com.example.twitturin.presentation.sealeds

import com.example.twitturin.presentation.data.registration.SignIn
import com.example.twitturin.presentation.data.users.ApiUsersItem

sealed class SignInResult {
    data class Success(val response: ApiUsersItem) : SignInResult()
    data class Error(val message: String) : SignInResult()
}