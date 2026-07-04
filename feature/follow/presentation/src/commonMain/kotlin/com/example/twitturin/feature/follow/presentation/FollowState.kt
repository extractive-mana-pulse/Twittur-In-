package com.example.twitturin.feature.follow.presentation

import com.example.twitturin.feature.follow.domain.FollowUser

/** Which list is being shown — also decides the trailing action (follow vs unfollow). */
enum class FollowListMode { FOLLOWERS, FOLLOWING }

data class FollowState(
    val mode: FollowListMode = FollowListMode.FOLLOWERS,
    val users: List<FollowUserUi> = emptyList(),
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
)

/** Display-ready row for a follower/following user. */
data class FollowUserUi(
    val id: String,
    val username: String,
    val fullName: String,
    val bio: String,
    val profilePicture: String?,
)

fun FollowUser.toFollowUserUi(): FollowUserUi = FollowUserUi(
    id = id,
    username = username,
    fullName = fullName ?: "Twittur User",
    bio = bio.orEmpty(),
    profilePicture = profilePicture,
)
