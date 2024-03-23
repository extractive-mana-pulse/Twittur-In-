package com.example.twitturin.follow.presentation.sealed

import com.example.twitturin.auth.presentation.model.data.User

sealed class UnFollow {
    data class Success(val user : User) : UnFollow()
    data class Error(val message: String) : UnFollow()
}