package com.example.twitturin.follow.presentation.followers.sealed

import com.example.twitturin.follow.domain.model.FollowUser

sealed class Follow {
    data class Success(val user : FollowUser) : Follow()
    data class Error(val message: String) : Follow()
}