package com.example.twitturin.feature.follow.domain

/** Domain model for a user in a followers / following list. */
data class FollowUser(
    val id: String,
    val username: String,
    val fullName: String?,
    val profilePicture: String?,
    val bio: String?,
)
