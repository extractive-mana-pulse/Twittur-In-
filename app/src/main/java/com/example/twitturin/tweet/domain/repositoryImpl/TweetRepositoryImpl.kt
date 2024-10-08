package com.example.twitturin.tweet.domain.repositoryImpl

import com.example.twitturin.tweet.domain.model.LikeTweet
import com.example.twitturin.tweet.domain.model.TweetContent
import com.example.twitturin.tweet.domain.model.ReplyContent
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.data.remote.repository.TweetRepository
import com.example.twitturin.tweet.data.remote.api.TweetApi
import retrofit2.Call
import retrofit2.Response

class TweetRepositoryImpl(
    private val tweetApi: TweetApi
) : TweetRepository {

    override suspend fun getTweet(): Response<List<Tweet>> {
        return tweetApi.getTweet()
    }

    override fun postTweet(tweet: TweetContent, token: String): Call<TweetContent> {
        return tweetApi.postTweet(tweet, token)
    }

    override suspend fun getPostsByUser(userId: String): Response<List<Tweet>> {
        return tweetApi.getPostsByUser(userId)
    }

    override suspend fun getRepliesOfPost(tweetId: String): Response<List<Tweet>> {
        return tweetApi.getRepliesOfPost(tweetId)
    }

    override fun postReply(
        reply: ReplyContent,
        tweetId: String,
        token: String
    ): Call<ReplyContent> {
        return tweetApi.postReply(reply, tweetId, token)
    }

    override fun editTweet(
        tweetContent: TweetContent,
        tweetId: String,
        token: String
    ): Call<TweetContent> {
        return tweetApi.editTweet(tweetContent, tweetId, token)
    }

    override suspend fun deleteTweet(tweetId: String, token: String): Response<Unit> {
        return tweetApi.deleteTweet(tweetId, token)
    }

    override suspend fun getListOfLikedPosts(userId: String): Response<List<Tweet>> {
        return tweetApi.getListOfLikedPosts(userId)
    }

    override fun like(tweet: LikeTweet, userId: String, token: String): Call<LikeTweet> {
        return tweetApi.like(tweet, userId, token)
    }

    override fun unLike(tweet: LikeTweet, userId: String, token: String): Call<LikeTweet> {
        return tweetApi.unLike(tweet, userId, token)
    }

    override suspend fun getTweetDescription(tweetId: String): Response<TweetContent> {
        return tweetApi.getTweetDescription(tweetId)
    }
}