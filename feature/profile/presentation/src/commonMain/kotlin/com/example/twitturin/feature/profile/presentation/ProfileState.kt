package com.example.twitturin.feature.profile.presentation

import com.example.twitturin.feature.profile.domain.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: ProfileUi? = null,
    /** Whether this profile belongs to the signed-in user (own profile UI vs visitor UI). */
    val isMe: Boolean = true,
    /** Visitor view: whether the signed-in user follows this profile; null while unknown. */
    val isFollowing: Boolean? = null,
    /** Own profile: a picture upload/removal is in flight. */
    val isUpdatingPhoto: Boolean = false,
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
