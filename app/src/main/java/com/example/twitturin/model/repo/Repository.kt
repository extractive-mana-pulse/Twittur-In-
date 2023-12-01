package com.example.twitturin.model.repo

import com.example.twitturin.model.data.tweets.ApiTweetsItem
import retrofit2.Response

class Repository {

    suspend fun getTweets() : Response<List<ApiTweetsItem>> {
        return com.example.twitturin.model.api.RetrofitInstance.api.getTweet()
    }
}