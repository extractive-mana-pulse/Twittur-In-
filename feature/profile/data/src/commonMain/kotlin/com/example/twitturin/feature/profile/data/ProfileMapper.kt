package com.example.twitturin.feature.profile.data

import com.example.twitturin.feature.profile.domain.EditProfile
import com.example.twitturin.feature.profile.domain.User

fun UserDto.toUser(): User = User(
    id = id.orEmpty(),
    username = username,
    fullName = fullName,
    email = email,
    bio = bio,
    country = country,
    birthday = birthday,
    profilePicture = profilePicture,
    kind = kind,
    studentId = studentId,
    major = major,
    age = age,
    followersCount = followersCount ?: 0,
    followingCount = followingCount ?: 0,
)

fun EditProfile.toDto(): EditProfileDto = EditProfileDto(
    fullName = fullName,
    username = username,
    email = email,
    bio = bio,
    country = country,
    birthday = birthday,
)
