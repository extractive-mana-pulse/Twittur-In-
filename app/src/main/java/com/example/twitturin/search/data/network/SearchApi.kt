package com.example.twitturin.search.data.network

import com.example.twitturin.search.data.model.SearchResponse
import com.example.twitturin.search.data.model.SearchUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search")
    suspend fun searchNews(@Query("keyword") keyword : String) : Response<SearchResponse>

}