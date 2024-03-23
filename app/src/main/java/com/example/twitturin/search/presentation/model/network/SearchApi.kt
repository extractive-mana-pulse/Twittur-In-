package com.example.twitturin.search.presentation.model.network

import com.example.twitturin.auth.presentation.model.data.Head
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search")
    suspend fun searchNews(@Query("keyword") keyword : String) : Response<Head>

}