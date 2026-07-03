package com.example.twitturin.feature.follow.data

import com.example.twitturin.core.data.network.delete
import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.data.network.post
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.follow.domain.FollowRepository
import com.example.twitturin.feature.follow.domain.FollowUser
import io.ktor.client.HttpClient

class FollowRepositoryImpl(
    private val httpClient: HttpClient,
) : FollowRepository {

    override suspend fun getFollowers(userId: String): Result<List<FollowUser>, DataError.Network> {
        return httpClient.get<List<FollowUserDto>>(route = "users/$userId/followers")
            .map { list -> list.map { it.toFollowUser() } }
    }

    override suspend fun getFollowing(userId: String): Result<List<FollowUser>, DataError.Network> {
        return httpClient.get<List<FollowUserDto>>(route = "users/$userId/following")
            .map { list -> list.map { it.toFollowUser() } }
    }

    // POST/DELETE following/{id}: body-less, authenticated; the echoed user is discarded (Unit).
    override suspend fun followUser(userId: String): EmptyResult<DataError.Network> {
        return httpClient.post<Unit>(route = "following/$userId")
    }

    override suspend fun unfollowUser(userId: String): EmptyResult<DataError.Network> {
        return httpClient.delete<Unit>(route = "following/$userId")
    }
}
