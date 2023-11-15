package com.example.twitturin.presentation.api

import com.example.twitturin.presentation.data.registration.SignIn
import com.example.twitturin.presentation.data.registration.SignUp
import com.example.twitturin.presentation.data.TheTweet
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.data.users.ApiUsersItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api  {

    @GET("users")
    fun getUserData(): Call<List<ApiUsersItem>>

    @POST("auth")
    fun signInUser(@Body authUSer: SignIn): Call<ApiUsersItem>

    @POST("users")
    fun signUpUser(@Body user: SignUp): Call<SignUp>

    @POST("tweets")
    fun postTweet(@Body tweet: TheTweet): Call<TheTweet>

    @GET("tweets")
    suspend fun getTweet(): Response<List<ApiTweetsItem>>


    @Headers("Content-Type: application/json")
    @GET("tweets")
    suspend fun getTweets(@Header("Authorization") token: String): Response<List<ApiTweetsItem>>
}