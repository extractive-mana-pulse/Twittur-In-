package com.example.twitturin.search.data.remote.api

import com.example.twitturin.search.domain.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search")
    suspend fun searchNews(@Query("keyword") keyword : String) : Response<SearchResponse>

}