package com.example.twitturin.feature.tweet.data

import com.example.twitturin.core.data.network.delete
import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.data.network.post
import com.example.twitturin.core.data.network.put
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.EmptyResult
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.tweet.domain.Reply
import com.example.twitturin.feature.tweet.domain.Tweet
import com.example.twitturin.feature.tweet.domain.TweetLiker
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

    override suspend fun getUserReplies(userId: String): Result<List<Tweet>, DataError.Network> {
        return httpClient.get<List<TweetDto>>(route = "users/$userId/replies")
            .map { list -> list.map { it.toTweet() } }
    }

    override suspend fun getUserLikes(userId: String): Result<List<Tweet>, DataError.Network> {
        return httpClient.get<List<TweetDto>>(route = "users/$userId/likes")
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

    override suspend fun getTweet(tweetId: String): Result<Tweet, DataError.Network> {
        return httpClient.get<TweetDto>(route = "tweets/$tweetId").map { it.toTweet() }
    }

    override suspend fun getReplies(tweetId: String): Result<List<Reply>, DataError.Network> {
        return httpClient.get<List<ReplyDto>>(route = "tweets/$tweetId/replies")
            .map { list -> list.map { it.toReply() } }
    }

    override suspend fun postReply(tweetId: String, content: String): EmptyResult<DataError.Network> {
        return httpClient.post<PostTweetRequestDto, Unit>(
            route = "tweets/$tweetId/replies",
            body = PostTweetRequestDto(content = content),
        )
    }

    override suspend fun postReplyToReply(replyId: String, content: String): EmptyResult<DataError.Network> {
        return httpClient.post<PostTweetRequestDto, Unit>(
            route = "replies/$replyId",
            body = PostTweetRequestDto(content = content),
        )
    }

    override suspend fun getLikers(tweetId: String): Result<List<TweetLiker>, DataError.Network> {
        return httpClient.get<List<TweetLikerDto>>(route = "tweets/$tweetId/likes")
            .map { list -> list.map { it.toTweetLiker() } }
    }

    override suspend fun likeTweet(tweetId: String, newCount: Int): EmptyResult<DataError.Network> {
        return httpClient.post<LikeRequestDto, Unit>(
            route = "tweets/$tweetId/likes",
            body = LikeRequestDto(count = newCount.toString()),
        )
    }

    override suspend fun unlikeTweet(tweetId: String, newCount: Int): EmptyResult<DataError.Network> {
        return httpClient.delete<LikeRequestDto, Unit>(
            route = "tweets/$tweetId/likes",
            body = LikeRequestDto(count = newCount.toString()),
        )
    }

    override suspend fun editTweet(tweetId: String, content: String): EmptyResult<DataError.Network> {
        return httpClient.put<PostTweetRequestDto, Unit>(
            route = "tweets/$tweetId",
            body = PostTweetRequestDto(content = content),
        )
    }
}
