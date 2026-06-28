package com.example.twitturin.feature.profile.domain

/** Domain model for a Twittur user profile (`GET users/{id}`). */
data class User(
    val id: String,
    val username: String?,
    val fullName: String?,
    val email: String?,
    val bio: String?,
    val country: String?,
    val birthday: String?,
    val profilePicture: String?,
    val kind: String?,
    val studentId: String?,
    val major: String?,
    val age: Int?,
    val followersCount: Int,
    val followingCount: Int,
)
