package com.example.twitturin.tweet.presentation.sealed

sealed class TweetDelete {
    data object Success : TweetDelete()
    data class Error(val message: String) : TweetDelete()
}