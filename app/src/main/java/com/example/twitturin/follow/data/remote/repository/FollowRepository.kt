package com.example.twitturin.follow.data.remote.repository

import com.example.twitturin.follow.domain.model.FollowUser
import retrofit2.Call
import retrofit2.Response

interface FollowRepository {

    suspend fun getListOfFollowers(userId : String) : Response<List<FollowUser>>

    suspend fun getListOfFollowing(userId : String) : Response<List<FollowUser>>

    fun followUser(
        id: String,
        token: String
    ) : Call<FollowUser>

    fun unFollowUser(
        id: String,
        token: String
    ) : Call<FollowUser>
}