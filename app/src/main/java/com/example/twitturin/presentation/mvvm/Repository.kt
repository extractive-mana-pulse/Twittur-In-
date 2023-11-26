package com.example.twitturin.presentation.mvvm

import com.example.twitturin.presentation.api.RetrofitInstance
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.data.users.UsersItem
import retrofit2.Call
import retrofit2.Response

class Repository {

    suspend fun getTweets() : Response<List<ApiTweetsItem>> {
        return RetrofitInstance.api.getTweet()
    }
}