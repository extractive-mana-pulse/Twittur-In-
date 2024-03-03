package com.example.twitturin.tweet.model.network

import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.replyToTweet.ReplyContent
import com.example.twitturin.tweet.model.data.Tweet
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

    /** get all tweets */
    @GET("tweets")
    suspend fun getTweet(): Response<List<Tweet>>

    /** post tweet */
    @POST("tweets")
    fun postTweet(@Body tweet: TweetContent, @Header("Authorization") token: String): Call<TweetContent>

    /** post reply to the tweet */
    @POST("tweets/{id}/replies")
    fun postReply(
        @Body reply: ReplyContent,
        @Path("id")tweetId: String,
        @Header("Authorization") token: String
    ): Call<ReplyContent>

    /** edit tweet */
    @PUT("tweets/{id}")
    fun editTweet(
        @Body tweetContent : TweetContent,
        @Path("id") tweetId : String,
        @Header("Authorization") token: String
    ): Call<TweetContent>

    /** delete tweet */
    @DELETE("tweets/{id}")
    suspend fun deleteTweet(@Path("id") tweetId: String, @Header("Authorization") token: String): Response<Unit>

}