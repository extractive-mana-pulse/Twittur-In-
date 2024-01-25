package com.example.twitturin.viewmodel

import com.example.twitturin.model.data.users.User

sealed class UsersResult {
    data class Success(val users: List<User>) : UsersResult()
    data class Error(val errorMessage: String) : UsersResult()
}
