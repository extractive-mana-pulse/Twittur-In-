package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.users.User

sealed class FollowersResult {
    data class Success(val user: List<User>) : FollowersResult()
    data class Error(val message: String) : FollowersResult()

}