package com.example.twitturin.follow.sealed

import com.example.twitturin.auth.model.data.User

sealed class FollowersResult {
    data class Success(val user: List<User>) : FollowersResult()
    data class Error(val message: String) : FollowersResult()

}