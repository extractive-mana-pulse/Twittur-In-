package com.example.twitturin.follow.presentation.sealed

import com.example.twitturin.User

sealed class FollowersResult {
    data class Success(val user: List<User>) : FollowersResult()
    data class Error(val message: String) : FollowersResult()

}