package com.example.twitturin.detail.domain.repositoryImpl

import com.example.twitturin.detail.data.remote.api.LOLApi
import com.example.twitturin.detail.data.remote.repository.LOLRepository
import com.example.twitturin.detail.domain.model.UserLikesAPost
import com.example.twitturin.tweet.domain.model.Tweet
import retrofit2.Response

class LOLRepositoryImpl(private val lolApi: LOLApi): LOLRepository {
    override suspend fun usersWhoLikesAPost(tweetId: String): Response<List<UserLikesAPost>> {
        return lolApi.usersWhoLikesAPost(tweetId)
    }
}