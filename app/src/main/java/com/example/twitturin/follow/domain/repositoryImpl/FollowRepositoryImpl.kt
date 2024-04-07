package com.example.twitturin.follow.domain.repositoryImpl

import com.example.twitturin.follow.domain.repository.FollowRepository
import com.example.twitturin.follow.presentation.model.network.FollowApi
import com.example.twitturin.User
import retrofit2.Call
import retrofit2.Response

class FollowRepositoryImpl(
    private val followApi: FollowApi
) : FollowRepository {
    override suspend fun getListOfFollowers(userId: String): Response<List<User>> {
        return followApi.getListOfFollowers(userId)
    }

    override suspend fun getListOfFollowing(userId: String): Response<List<User>> {
        return followApi.getListOfFollowing(userId)
    }

    override fun followUser(id: String, token: String): Call<User> {
        return followApi.followUser(id, token)
    }

    override fun deleteFollow(id: String, token: String): Call<User> {
        return followApi.deleteFollow(id, token)
    }
}