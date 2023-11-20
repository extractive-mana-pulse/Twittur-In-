package com.example.twitturin.presentation.data.users

data class UsersItem(
    val age: Int,
    val country: String,
    val email: String,
    val fullName: String,
    val id: String,
    val kind: String,
    val major: String,
    val replies: List<Reply>,
    val studentId: String,
    val subject: String,
    val tweets: List<Tweet>,
    val username: String,
    val token: String?
)