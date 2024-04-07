package com.example.twitturin.search.domain.model

data class SearchUser(

    val username: String,
    val fullName: String,
    val profilePicture: String,
    val followingCount: Int,
    val followersCount: Int,
    val bio: String,
    val id: String,
    val token: String

)