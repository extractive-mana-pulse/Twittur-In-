package com.example.twitturin.detail.presentation.sealed

import com.example.twitturin.tweet.domain.model.ReplyContent


sealed class PostReply {
    data class Success(val response: ReplyContent) : PostReply()
    data class Error(val message: String) : PostReply()
    data object Loading : PostReply()
}