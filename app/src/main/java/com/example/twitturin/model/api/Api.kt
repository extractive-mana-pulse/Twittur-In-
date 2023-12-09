package com.example.twitturin.model.api

import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.data.likeTweet.LikeTweet
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.registration.Login
import com.example.twitturin.model.data.registration.SignUpStudent
import com.example.twitturin.model.data.registration.SignUpProf
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface Api  {

    // delete account both users.
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: String, @Header("Authorization") token: String): Response<Unit>

    // post tweet for both users.
    @POST("tweets")
    fun postLike(@Body tweet: LikeTweet, @Header("Authorization") token: String): Call<LikeTweet>

    // sign in.
    @POST("auth")
    fun signInUser(@Body authUSer: Login): Call<User>

    // get user tweets in tweets fragment.
    @GET("users/{id}")
    suspend fun getPostsByUser(@Query("userId") userId: String, @Header("Authorization") authToken: String): User

    // professor registration.
    @POST("users")
    fun signUpProf(@Body user: SignUpProf): Call<SignUpProf>

    // student registration.
    @POST("users")
    fun signUpStudent(@Body user: SignUpStudent): Call<SignUpStudent>

    // publish tweet both users.
    @POST("tweets")
    fun postTweet(@Body tweet: TweetContent, @Header("Authorization") token: String): Call<TweetContent>

    // get all tweets.
    @GET("tweets")
    suspend fun getTweet(): Response<List<Tweet>>

    @GET("users/{id}")
    suspend fun getLoggedInUserData(@Path("id") userId : String) : Response<User>

    @PUT("users/{id}")
    fun editUser(@Body edit : EditProfile, @Path("id") userId: String, @Header("Authorization") token: String): Call<EditProfile>

    @PATCH("users/{id}")
    suspend fun editUser2(@Path("id") userId: String, @Header("Authorization") token: String): Response<Unit>
}