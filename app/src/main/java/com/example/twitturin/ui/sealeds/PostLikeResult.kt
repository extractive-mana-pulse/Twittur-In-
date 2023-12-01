package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.likeTweet.LikeTweet

sealed class PostLikeResult {
    data class Success(val response: LikeTweet) : PostLikeResult()
    data class Error(val message: String) : PostLikeResult()
}
