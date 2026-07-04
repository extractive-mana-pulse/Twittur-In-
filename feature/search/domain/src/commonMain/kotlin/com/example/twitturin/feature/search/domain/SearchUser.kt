package com.example.twitturin.feature.search.domain

/** Domain model for a user returned by the search endpoint. */
data class SearchUser(
    val id: String,
    val username: String,
    val fullName: String?,
    val profilePicture: String?,
    val bio: String?,
    val kind: String?,
    val country: String?,
    val followersCount: Int,
    val followingCount: Int,
)
