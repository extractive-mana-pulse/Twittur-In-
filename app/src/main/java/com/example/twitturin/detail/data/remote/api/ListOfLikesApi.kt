package com.example.twitturin.detail.data.remote.api

import com.example.twitturin.detail.domain.model.UserLikesAPost
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ListOfLikesApi {

    @GET("tweets/{id}/likes")
    suspend fun usersWhoLikesAPost(@Path("id") tweetId : String) : Response<List<UserLikesAPost>>
}