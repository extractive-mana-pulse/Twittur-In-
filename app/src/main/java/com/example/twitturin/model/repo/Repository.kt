package com.example.twitturin.model.repo

import com.example.twitturin.model.api.RetrofitInstance
import com.example.twitturin.model.data.tweets.ApiTweetsItem
import com.example.twitturin.model.data.users.UsersItem
import retrofit2.Response

class Repository {

    suspend fun getTweets() : Response<List<ApiTweetsItem>> {
        return RetrofitInstance.api.getTweet()
    }

    suspend fun getAuthUserCredentials(userId: String) : Response<List<UsersItem>> {
        return RetrofitInstance.api.getLoggedInUserData(userId)
    }
}