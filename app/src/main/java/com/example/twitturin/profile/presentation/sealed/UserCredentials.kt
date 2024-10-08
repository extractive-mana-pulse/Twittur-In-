package com.example.twitturin.profile.presentation.sealed

import com.example.twitturin.profile.domain.model.User

sealed class UserCredentials {

    data class Success(val user : User) : UserCredentials()
    data class Error(val message: String) : UserCredentials()
    data object Loading: UserCredentials()

}