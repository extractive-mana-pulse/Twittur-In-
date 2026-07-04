package com.example.twitturin.feature.tweet.domain

/** A user who liked a tweet (`GET tweets/{id}/likes`). */
data class TweetLiker(
    val id: String,
    val username: String?,
    val fullName: String?,
    val profilePicture: String?,
    val bio: String?,
)
