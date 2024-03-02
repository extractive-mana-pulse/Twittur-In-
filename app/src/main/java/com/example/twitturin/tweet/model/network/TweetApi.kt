package com.example.twitturin.tweet.model.network

import com.example.twitturin.model.data.publicTweet.TweetContent
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface TweetApi {

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