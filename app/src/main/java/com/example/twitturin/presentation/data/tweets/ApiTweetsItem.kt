package com.example.twitturin.presentation.data.tweets

data class ApiTweetsItem(
    val author: Author,
    val content: String,
    val createdAt: String,
    val id: String,
    val likes: List<Like>,
    val updatedAt: String
)