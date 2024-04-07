package com.example.twitturin.search.data.remote.repository

import com.example.twitturin.search.domain.model.SearchResponse
import com.example.twitturin.search.domain.model.SearchUser
import retrofit2.Response

interface SearchRepository {
    suspend fun searchNews(keyword : String) : Response<SearchResponse>

}