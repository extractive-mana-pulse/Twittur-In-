package com.example.twitturin.presentation.data.tweets

data class Reply(
    val author: Author,
    val content: String,
    val createdAt: String,
    val id: String,
    val tweet: String,
    val updatedAt: String
)