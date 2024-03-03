package com.example.twitturin.tweet.model.domain.repository

import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.replyToTweet.ReplyContent
import com.example.twitturin.tweet.model.data.Tweet
import retrofit2.Call
import retrofit2.Response

interface TweetRepository {

    suspend fun getTweet(): Response<List<Tweet>>

    fun postTweet(
        tweet: TweetContent,
        token: String
    ): Call<TweetContent>

    fun postReply(
        reply: ReplyContent,
        tweetId: String,
        token: String
    ): Call<ReplyContent>

    fun editTweet(
        tweetContent : TweetContent,
        tweetId : String,
        token: String
    ): Call<TweetContent>

    suspend fun deleteTweet(
        tweetId: String,
        token: String
    ): Response<Unit>

}