package com.example.twitturin.tweet.presentation.detail.sealed

import com.example.twitturin.tweet.domain.model.ReplyContent


sealed class PostReply {
    data class Success(val response: ReplyContent) : PostReply()
    data class Error(val message: String) : PostReply()
}