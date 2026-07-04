package com.example.twitturin.feature.profile.domain

/** Editable profile fields (`PUT users/{id}`). */
data class EditProfile(
    val fullName: String,
    val username: String,
    val email: String,
    val bio: String,
    val country: String,
    val birthday: String,
)
