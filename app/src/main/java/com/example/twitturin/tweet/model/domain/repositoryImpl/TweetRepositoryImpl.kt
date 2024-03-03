package com.example.twitturin.tweet.model.domain.repositoryImpl

import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.replyToTweet.ReplyContent
import com.example.twitturin.tweet.model.data.Tweet
import com.example.twitturin.tweet.model.domain.repository.TweetRepository
import com.example.twitturin.tweet.model.network.TweetApi
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
}