package com.example.twitturin.feature.auth.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
)

@Serializable
data class AuthUserDto(
    @SerialName("id") val id: String? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class StudentRegistrationDto(
    @SerialName("fullName") val fullName: String,
    @SerialName("username") val username: String,
    @SerialName("studentId") val studentId: String,
    @SerialName("major") val major: String,
    @SerialName("password") val password: String,
    @SerialName("kind") val kind: String,
)

@Serializable
data class ProfessorRegistrationDto(
    @SerialName("fullName") val fullName: String,
    @SerialName("username") val username: String,
    @SerialName("subject") val subject: String,
    @SerialName("password") val password: String,
    @SerialName("kind") val kind: String,
)
