package com.example.twitturin.feature.tweet.presentation.likes

import com.example.twitturin.feature.tweet.domain.TweetLiker

data class LikesListState(
    val likers: List<LikerUi> = emptyList(),
    val isLoading: Boolean = false,
)

data class LikerUi(
    val id: String,
    val fullName: String,
    val username: String,
    val avatar: String?,
)

fun TweetLiker.toLikerUi(): LikerUi = LikerUi(
    id = id,
    fullName = fullName ?: "Twittur User",
    username = username.orEmpty(),
    avatar = profilePicture,
)
