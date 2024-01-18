package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.users.User

sealed class FollowResult {
    data class Success(val username : User) : FollowResult()
    data class Error(val message: String) : FollowResult()
}