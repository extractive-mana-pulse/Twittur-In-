package com.example.twitturin.feature.follow.data

import com.example.twitturin.feature.follow.domain.FollowUser

fun FollowUserDto.toFollowUser(): FollowUser = FollowUser(
    id = id.orEmpty(),
    username = username.orEmpty(),
    fullName = fullName,
    profilePicture = profilePicture,
    bio = bio,
)
