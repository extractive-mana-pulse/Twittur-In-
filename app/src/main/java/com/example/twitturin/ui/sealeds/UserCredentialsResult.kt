package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.users.User

sealed class UserCredentialsResult {

    data class Success(val user : User) : UserCredentialsResult()
    data class Error(val message: String) : UserCredentialsResult()

}