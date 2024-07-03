package com.example.twitturin.detail.domain.repositoryImpl

import com.example.twitturin.detail.data.remote.api.ListOfLikesApi
import com.example.twitturin.detail.data.remote.repository.ListOfLikesRepository
import com.example.twitturin.detail.domain.model.UserLikesAPost
import retrofit2.Response

class ListOfLikesRepositoryImpl(private val lolApi: ListOfLikesApi): ListOfLikesRepository {
    override suspend fun usersWhoLikesAPost(tweetId: String): Response<List<UserLikesAPost>> {
        return lolApi.usersWhoLikesAPost(tweetId)
    }
}