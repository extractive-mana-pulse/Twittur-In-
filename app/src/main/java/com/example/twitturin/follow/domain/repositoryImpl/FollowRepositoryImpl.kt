package com.example.twitturin.follow.domain.repositoryImpl

import com.example.twitturin.follow.data.remote.api.FollowApi
import com.example.twitturin.follow.data.remote.repository.FollowRepository
import com.example.twitturin.follow.domain.model.FollowUser
import retrofit2.Call
import retrofit2.Response

class FollowRepositoryImpl(private val followApi: FollowApi) : FollowRepository {

    override suspend fun getListOfFollowers(userId: String): Response<List<FollowUser>> = followApi.getListOfFollowers(userId)
    override suspend fun getListOfFollowing(userId: String): Response<List<FollowUser>> = followApi.getListOfFollowing(userId)
    override fun followUser(id: String, token: String): Call<FollowUser> = followApi.followUser(id, token)
    override fun unFollowUser(id: String, token: String): Call<FollowUser> = followApi.unFollowUser(id, token)

}