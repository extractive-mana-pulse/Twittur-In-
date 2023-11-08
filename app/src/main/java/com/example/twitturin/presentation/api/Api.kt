package com.example.twitturin.presentation.api

import com.example.twitturin.presentation.data.SignIn
import com.example.twitturin.presentation.data.SignUp
import com.example.twitturin.presentation.data.TheTweet
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.data.users.ApiUsersItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api  {

    @GET("users")
    fun getUserData(): Call<List<ApiUsersItem>>

    @POST("auth")
    fun signInUser(@Body authUSer: SignIn): Call<SignIn>

    @POST("users")
    fun signUpUser(@Body user: SignUp): Call<SignUp>

    @POST("tweets")
    fun postTweet(@Body tweet: TheTweet): Call<TheTweet>

    @GET("tweets")
    suspend fun getTweet(): Response<List<ApiTweetsItem>>
}