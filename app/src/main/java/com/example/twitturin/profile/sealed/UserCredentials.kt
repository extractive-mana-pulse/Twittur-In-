package com.example.twitturin.profile.sealed

import com.example.twitturin.model.data.users.User

sealed class UserCredentials {

    data class Success(val user : User) : UserCredentials()
    data class Error(val message: String) : UserCredentials()

}