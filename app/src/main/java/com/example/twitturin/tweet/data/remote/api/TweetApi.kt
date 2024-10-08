package com.example.twitturin.tweet.data.remote.api

import com.example.twitturin.tweet.domain.model.LikeTweet
import com.example.twitturin.tweet.domain.model.ReplyContent
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.domain.model.TweetContent
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TweetApi {

    @GET("tweets")
    suspend fun getTweet(): Response<List<Tweet>>

    @POST("tweets")
    fun postTweet(
        @Body tweet: TweetContent,
        @Header("Authorization") token: String
    ): Call<TweetContent>

    @GET("users/{id}/tweets")
    suspend fun getPostsByUser(@Path("id") userId: String): Response<List<Tweet>>

    @GET("tweets/{id}/replies")
    suspend fun getRepliesOfPost(@Path("id") tweetId: String): Response<List<Tweet>>

    @POST("tweets/{id}/replies")
    fun postReply(
        @Body reply: ReplyContent,
        @Path("id") tweetId: String,
        @Header("Authorization") token: String
    ): Call<ReplyContent>

    @PUT("tweets/{id}")
    fun editTweet(
        @Body tweetContent: TweetContent,
        @Path("id") tweetId: String,
        @Header("Authorization") token: String
    ): Call<TweetContent>

    @DELETE("tweets/{id}")
    suspend fun deleteTweet(
        @Path("id") tweetId: String,
        @Header("Authorization") token: String
    ): Response<Unit>

    @DELETE("replies/{id}")
    suspend fun deleteReply(
        @Path("id") replyId: String,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("users/{id}/likes")
    suspend fun getListOfLikedPosts(@Path("id") userId: String): Response<List<Tweet>>

    @POST("tweets/{id}/likes")
    fun like(
        @Body tweet: LikeTweet,
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Call<LikeTweet>

    @DELETE("tweets/{id}/likes")
    fun unLike(
        @Body tweet: LikeTweet,
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Call<LikeTweet>

    @GET("tweets/{id}")
    suspend fun getTweetDescription(@Path("id") tweetId: String): Response<TweetContent>
}