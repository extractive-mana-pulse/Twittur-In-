package com.example.twitturin.profile.presentation.sealed

import com.example.twitturin.auth.presentation.model.data.User

sealed class UsersResult {
    data class Success(val users: List<User>) : UsersResult()
    data class Error(val errorMessage: String) : UsersResult()
}
