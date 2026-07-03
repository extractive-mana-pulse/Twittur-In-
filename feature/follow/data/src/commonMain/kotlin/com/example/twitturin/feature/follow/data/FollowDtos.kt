package com.example.twitturin.feature.follow.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A user as returned by `GET users/{id}/followers` and `GET users/{id}/following`. */
@Serializable
data class FollowUserDto(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null,
    @SerialName("bio") val bio: String? = null,
)
