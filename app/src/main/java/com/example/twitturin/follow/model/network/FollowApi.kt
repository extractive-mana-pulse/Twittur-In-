package com.example.twitturin.follow.model.network

import com.example.twitturin.auth.model.data.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FollowApi {

    @GET("users/{id}/followers")
    suspend fun getListOfFollowers(@Path("id")userId : String) : Response<List<User>>

    @GET("users/{id}/following")
    suspend fun getListOfFollowing(@Path("id")userId : String) : Response<List<User>>

    @POST("following/{id}")
    fun followUser(@Path("id") id: String, @Header("Authorization") token: String) : Call<User>

    @DELETE("following/{id}")
    fun deleteFollow(@Path("id") id: String, @Header("Authorization") token: String) : Call<User>
}