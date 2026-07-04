package com.example.twitturin.feature.profile.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null,
    @SerialName("kind") val kind: String? = null,
    @SerialName("studentId") val studentId: String? = null,
    @SerialName("major") val major: String? = null,
    @SerialName("age") val age: Int? = null,
    @SerialName("followersCount") val followersCount: Int? = null,
    @SerialName("followingCount") val followingCount: Int? = null,
)

@Serializable
data class EditProfileDto(
    @SerialName("fullName") val fullName: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("bio") val bio: String,
    @SerialName("country") val country: String,
    @SerialName("birthday") val birthday: String,
)
