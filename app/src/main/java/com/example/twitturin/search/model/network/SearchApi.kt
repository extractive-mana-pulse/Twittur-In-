package com.example.twitturin.search.model.network

import com.example.twitturin.auth.model.data.Head
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.tweet.model.data.Tweet
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search")
    suspend fun searchNews(@Query("keyword") keyword : String) : Response<Head>

}