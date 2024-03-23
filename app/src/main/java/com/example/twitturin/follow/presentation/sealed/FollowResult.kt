package com.example.twitturin.follow.presentation.sealed

import com.example.twitturin.auth.presentation.model.data.User

sealed class FollowResult {
    data class Success(val user : User) : FollowResult()
    data class Error(val message: String) : FollowResult()
}