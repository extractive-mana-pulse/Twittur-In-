package com.example.twitturin.feature.profile.presentation

import com.example.twitturin.feature.profile.domain.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: ProfileUi? = null,
)

/** Display-ready profile model (also holds the raw fields the edit form prefills from). */
data class ProfileUi(
    val id: String,
    val fullName: String,
    val username: String,
    val email: String,
    val bio: String,
    val country: String,
    val birthday: String,
    val profilePicture: String?,
    val kind: String?,
    val studentId: String?,
    val followersCount: Int,
    val followingCount: Int,
)

fun User.toProfileUi(): ProfileUi = ProfileUi(
    id = id,
    fullName = fullName ?: "Twittur User",
    username = username.orEmpty(),
    email = email.orEmpty(),
    bio = bio.orEmpty(),
    country = country.orEmpty(),
    birthday = birthday.orEmpty(),
    profilePicture = profilePicture,
    kind = kind,
    studentId = studentId,
    followersCount = followersCount,
    followingCount = followingCount,
)
