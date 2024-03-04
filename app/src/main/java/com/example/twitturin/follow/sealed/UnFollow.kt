package com.example.twitturin.follow.sealed

import com.example.twitturin.auth.model.data.User

sealed class UnFollow {
    data class Success(val user : User) : UnFollow()
    data class Error(val message: String) : UnFollow()
}