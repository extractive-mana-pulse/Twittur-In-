package com.example.twitturin.feature.tweet.domain

/** Domain model for a single tweet (`GET tweets`, `GET users/{id}/tweets`). */
data class Tweet(
    val id: String,
    val content: String,
    val author: TweetAuthor?,
    val createdAt: String?,
    val likes: Int,
    val replyCount: Int,
    val likedBy: List<String>,
    val isEdited: Boolean,
)

/** The user who wrote a tweet (a trimmed view of the full profile). */
data class TweetAuthor(
    val id: String,
    val username: String?,
    val fullName: String?,
    val profilePicture: String?,
)
