package com.example.twitturin.follow.data.remote.api

import com.example.twitturin.follow.domain.model.FollowUser
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FollowApi {

    @GET("users/{id}/followers")
    suspend fun getListOfFollowers(@Path("id")userId : String) : Response<List<FollowUser>>

    @GET("users/{id}/following")
    suspend fun getListOfFollowing(@Path("id")userId : String) : Response<List<FollowUser>>

    @POST("following/{id}")
    fun followUser(@Path("id") id: String, @Header("Authorization") token: String) : Call<FollowUser>

    @DELETE("following/{id}")
    fun unFollowUser(@Path("id") id: String, @Header("Authorization") token: String) : Call<FollowUser>
}