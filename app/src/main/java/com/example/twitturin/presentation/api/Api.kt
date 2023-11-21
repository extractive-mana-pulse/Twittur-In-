package com.example.twitturin.presentation.api

import com.example.twitturin.presentation.data.registration.SignIn
import com.example.twitturin.presentation.data.registration.SignUp
import com.example.twitturin.presentation.data.publicTweet.TheTweet
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.data.users.UsersItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface Api  {

    @GET("users")
    fun getUserData(): Call<List<UsersItem>>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: Int,@Header("Authorization") token: String): Response<Unit>

    @POST("auth")
    fun signInUser(@Body authUSer: SignIn): Call<UsersItem>

    @POST("users")
    fun signUpUser(@Body user: SignUp): Call<SignUp>

    @POST("tweets")
    fun postTweet(@Body tweet: TheTweet, @Header("Authorization") token: String): Call<TheTweet>

    @GET("tweets")
    suspend fun getTweet(): Response<List<ApiTweetsItem>>

    @GET("users")
    fun getAuthUserData(@Header("Authorization") token: String) : Call<List<UsersItem>>
}