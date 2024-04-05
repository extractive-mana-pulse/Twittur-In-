package com.example.twitturin.search.data.repository

import com.example.twitturin.search.data.model.SearchResponse
import com.example.twitturin.search.data.model.SearchUser
import retrofit2.Response

interface SearchRepository {
    suspend fun searchNews(keyword : String) : Response<SearchResponse>

}