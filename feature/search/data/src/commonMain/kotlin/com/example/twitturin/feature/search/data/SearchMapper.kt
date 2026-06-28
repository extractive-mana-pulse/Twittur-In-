package com.example.twitturin.feature.search.data

import com.example.twitturin.feature.search.domain.SearchUser

fun SearchUserDto.toSearchUser(): SearchUser = SearchUser(
    id = id.orEmpty(),
    username = username.orEmpty(),
    fullName = fullName,
    profilePicture = profilePicture,
    bio = bio,
    kind = kind,
    country = country,
    followersCount = followersCount ?: 0,
    followingCount = followingCount ?: 0,
)
