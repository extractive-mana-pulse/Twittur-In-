package com.example.twitturin.feature.follow.presentation

sealed interface FollowAction {
    data class OnUserClick(val userId: String) : FollowAction
    /** Trailing button: follow (on the followers list) or unfollow (on the following list). */
    data class OnActionClick(val userId: String) : FollowAction
}
