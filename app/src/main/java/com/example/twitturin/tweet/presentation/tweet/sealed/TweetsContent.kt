package com.example.twitturin.tweet.presentation.tweet.sealed

import com.example.twitturin.tweet.domain.model.TweetContent

sealed class TweetsContent {
    data class Success(val tweet: TweetContent) : TweetsContent()
    data class Error(val errorMessage: String) : TweetsContent()
}