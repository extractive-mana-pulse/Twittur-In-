package com.example.twitturin.follow.domain.repository

import com.example.twitturin.auth.model.data.User
import retrofit2.Call
import retrofit2.Response

interface FollowRepository {

    suspend fun getListOfFollowers(userId : String) : Response<List<User>>

    suspend fun getListOfFollowing(userId : String) : Response<List<User>>

    fun followUser(
        id: String,
        token: String
    ) : Call<User>

    fun deleteFollow(
        id: String,
        token: String
    ) : Call<User>

}