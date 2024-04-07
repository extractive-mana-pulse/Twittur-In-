package com.example.twitturin.tweet.presentation.sealed

import com.example.twitturin.tweet.domain.model.TweetContent

sealed class PostTweet{
    data class Success(val response: TweetContent) : PostTweet()
    data class Error(val message: String) : PostTweet()
}
