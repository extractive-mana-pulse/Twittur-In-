package com.example.twitturin.follow.presentation.following.sealed

import com.example.twitturin.follow.domain.model.FollowUser

sealed class UnFollow {
    data class Success(val user : FollowUser) : UnFollow()
    data class Error(val message: String) : UnFollow()
    data object Loading: UnFollow()
}