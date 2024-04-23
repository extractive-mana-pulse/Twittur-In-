package com.example.twitturin.tweet.presentation.like.sealed

import com.example.twitturin.tweet.domain.model.LikeTweet

sealed class PostLike {
    data class Success(val response: LikeTweet) : PostLike()
    data class Error(val message: String) : PostLike()
}
