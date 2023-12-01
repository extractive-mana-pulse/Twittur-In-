package com.example.twitturin.model.data.users

data class Tweet(
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