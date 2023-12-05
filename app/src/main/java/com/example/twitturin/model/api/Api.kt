package com.example.twitturin.model.api

import com.example.twitturin.model.data.likeTweet.LikeTweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.registration.Login
import com.example.twitturin.model.data.registration.SignUp
import com.example.twitturin.model.data.tweets.Tweet
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api  {

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String, @Header("Authorization") token: String): Response<Unit>

    @POST("tweets")
    fun postLike(@Body tweet: LikeTweet, @Header("Authorization") token: String): Call<LikeTweet>

    @POST("auth")
    fun signInUser(@Body authUSer: Login): Call<User>

    @GET("users/{id}")
    suspend fun getPostsByUser(@Query("userId") userId: String, @Header("Authorization") authToken: String): User

    @POST("users")
    fun signUpUser(@Body user: SignUp): Call<SignUp>

    @POST("tweets")
    fun postTweet(@Body tweet: TweetContent, @Header("Authorization") token: String): Call<TweetContent>

    @GET("tweets")
    suspend fun getTweet(): Response<List<Tweet>>

    @GET("users/{id}")
    suspend fun getLoggedInUserData(@Query("userId") userId : String) : Response<List<User>>
}