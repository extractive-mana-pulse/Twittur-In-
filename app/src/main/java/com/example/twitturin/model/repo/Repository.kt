package com.example.twitturin.model.repo

import com.example.twitturin.auth.model.data.User
import com.example.twitturin.model.network.RetrofitInstance
import com.example.twitturin.tweet.model.data.Tweet
import retrofit2.Response

class Repository {

    suspend fun getAllUsers(): Response<List<User>> {
        return RetrofitInstance.api.getAllUsers()
    }

    suspend fun searchNews(searchQuery: Tweet) = RetrofitInstance.api.searchNews(searchQuery)
}