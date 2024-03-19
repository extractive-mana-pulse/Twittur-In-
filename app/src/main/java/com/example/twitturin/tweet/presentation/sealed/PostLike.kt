package com.example.twitturin.tweet.presentation.sealed

import com.example.twitturin.tweet.data.data.LikeTweet

sealed class PostLike {
    data class Success(val response: LikeTweet) : PostLike()
    data class Error(val message: String) : PostLike()
}
