package com.example.twitturin.model.repo

import com.example.twitturin.model.network.RetrofitInstance
import com.example.twitturin.model.data.tweets.Tweet
import retrofit2.Response

class Repository {

    suspend fun getTweets() : Response<List<Tweet>> {
        return RetrofitInstance.api.getTweet()
    }

    suspend fun getUserTweets(userId: String) : Response<List<Tweet>> {
        return RetrofitInstance.api.getPostsByUser(userId)
    }

//    suspend fun getLoggedInUserData(userId: String) : Response<List<User>> {
//        return RetrofitInstance.api.getLoggedInUserData(userId)
//    }
}