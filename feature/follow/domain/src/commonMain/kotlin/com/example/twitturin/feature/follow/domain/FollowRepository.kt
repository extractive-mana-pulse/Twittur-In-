package com.example.twitturin.feature.follow.domain

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result

/**
 * Contract for the followers / following lists and the follow/unfollow actions.
 * Implemented in :feature:follow:data over Ktor. follow/unfollow are authenticated
 * (the bearer token is attached by core:data's Auth plugin for the twitturin host).
 */
interface FollowRepository {
    suspend fun getFollowers(userId: String): Result<List<FollowUser>, DataError.Network>
    suspend fun getFollowing(userId: String): Result<List<FollowUser>, DataError.Network>
    suspend fun followUser(userId: String): EmptyResult<DataError.Network>
    suspend fun unfollowUser(userId: String): EmptyResult<DataError.Network>
}
