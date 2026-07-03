package com.example.twitturin.search.data.remote.repository

import com.example.twitturin.search.domain.model.SearchResponse
import retrofit2.Response

interface SearchRepository {
    suspend fun searchUser(keyword : String) : Response<SearchResponse>

}