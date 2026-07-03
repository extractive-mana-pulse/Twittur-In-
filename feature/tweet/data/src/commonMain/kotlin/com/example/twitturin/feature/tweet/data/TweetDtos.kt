package com.example.twitturin.feature.tweet.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TweetDto(
    @SerialName("id") val id: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("author") val author: TweetAuthorDto? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("likes") val likes: Int? = null,
    // The backend has used both spellings across versions; accept either.
    @SerialName("likedBy") @JsonNames("likesBy") val likedBy: List<String>? = null,
    @SerialName("replyCount") val replyCount: Int? = null,
    @SerialName("isEdited") val isEdited: Boolean? = null,
)

@Serializable
data class TweetAuthorDto(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null,
)

@Serializable
data class PostTweetRequestDto(
    @SerialName("content") val content: String,
)

/** Body for like/unlike — the resulting like count (the backend expects a string). */
@Serializable
data class LikeRequestDto(
    @SerialName("count") val count: String,
)

@Serializable
data class TweetLikerDto(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null,
    @SerialName("bio") val bio: String? = null,
)
