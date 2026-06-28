package com.example.twitturin.feature.tweet.data

import com.example.twitturin.core.data.network.delete
import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.data.network.post
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.tweet.domain.Tweet
import com.example.twitturin.feature.tweet.domain.TweetRepository
import io.ktor.client.HttpClient

class TweetRepositoryImpl(
    private val httpClient: HttpClient,
) : TweetRepository {

    override suspend fun getFeed(): Result<List<Tweet>, DataError.Network> {
        return httpClient.get<List<TweetDto>>(route = "tweets")
            .map { list -> list.map { it.toTweet() } }
    }

    override suspend fun getUserTweets(userId: String): Result<List<Tweet>, DataError.Network> {
        return httpClient.get<List<TweetDto>>(route = "users/$userId/tweets")
            .map { list -> list.map { it.toTweet() } }
    }

    override suspend fun postTweet(content: String): EmptyResult<DataError.Network> {
        // Response type is Unit: we only need success/failure, so the echoed-back tweet is discarded.
        return httpClient.post<PostTweetRequestDto, Unit>(
            route = "tweets",
            body = PostTweetRequestDto(content = content),
        )
    }

    override suspend fun deleteTweet(tweetId: String): EmptyResult<DataError.Network> {
        return httpClient.delete<Unit>(route = "tweets/$tweetId")
    }
}
