package com.example.twitturin.feature.search.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("users") val users: List<SearchUserDto> = emptyList(),
)

@Serializable
data class SearchUserDto(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("kind") val kind: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("followersCount") val followersCount: Int? = null,
    @SerialName("followingCount") val followingCount: Int? = null,
)
