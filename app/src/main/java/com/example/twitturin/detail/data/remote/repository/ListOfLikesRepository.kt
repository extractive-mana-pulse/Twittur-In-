package com.example.twitturin.detail.data.remote.repository

import com.example.twitturin.detail.domain.model.UserLikesAPost
import retrofit2.Response

interface ListOfLikesRepository {
    suspend fun usersWhoLikesAPost(tweetId : String) : Response<List<UserLikesAPost>>
}