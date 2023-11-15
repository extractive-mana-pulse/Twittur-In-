package com.example.twitturin.presentation.data.users

data class ApiUsersItem(
    val age: Int,
    val country: String,
    val email: String,
    val fullName: String,
    val id: String,
    val major: String,
    val studentId: String,
    val tweets: List<Tweet>,
    val username: String,
    val token: String?
)