package com.example.twitturin.tweet.data.remote.repository

import com.example.twitturin.tweet.domain.model.LikeTweet
import com.example.twitturin.tweet.domain.model.ReplyContent
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.domain.model.TweetContent
import retrofit2.Call
import retrofit2.Response

/** Note: this TweetRepository interface has all information regarding to the tweet. Like, comment, reply, share and other! */

interface TweetRepository {

    suspend fun getTweet(): Response<List<Tweet>>

    fun postTweet(tweet: TweetContent, token: String): Call<TweetContent>

    suspend fun getPostsByUser(userId: String): Response<List<Tweet>>

    suspend fun getRepliesOfPost(tweetId : String) : Response<List<Tweet>>

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

    suspend fun getListOfLikedPosts(userId : String) : Response<List<Tweet>>

    fun like(
        tweet : LikeTweet,
        userId : String,
        token: String
    ) : Call<LikeTweet>

    fun unLike(
        tweet: LikeTweet,
        userId: String,
        token: String
    ) : Call<LikeTweet>

    suspend fun getTweetDescription(tweetId: String): Response<TweetContent>
}