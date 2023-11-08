package com.example.twitturin.presentation.data.tweets

data class ApiTweetsItem(
    val author: Author,
    val content: String,
    val createdAt: String,
    val id: String,
    val likedBy: List<Any>,
    val likes: Int,
    val updatedAt: String
)