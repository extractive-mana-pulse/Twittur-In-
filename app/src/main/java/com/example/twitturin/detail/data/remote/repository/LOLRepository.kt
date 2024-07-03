package com.example.twitturin.detail.data.remote.repository

import com.example.twitturin.detail.domain.model.UserLikesAPost
import com.example.twitturin.tweet.domain.model.Tweet
import retrofit2.Response

interface LOLRepository {
    suspend fun usersWhoLikesAPost(tweetId : String) : Response<List<UserLikesAPost>>
}