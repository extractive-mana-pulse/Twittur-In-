package com.example.twitturin.presentation.data.users

data class Tweet(
    val author: String,
    val content: String,
    val createdAt: String,
    val id: String,
    val likes: List<String>,
    val updatedAt: String
)