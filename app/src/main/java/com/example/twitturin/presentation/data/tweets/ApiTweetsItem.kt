package com.example.twitturin.presentation.data.tweets

data class ApiTweetsItem(
    val author: Author,
    val content: String,
    val createdAt: String,
    val id: String,
    val likedBy: List<LikedBy>,
    val likes: Int,
    val replies: List<Reply>,
    val replyCount: Int,
    val updatedAt: String
)