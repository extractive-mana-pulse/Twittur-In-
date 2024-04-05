package com.example.twitturin.search.domain.repositoryImpl

import com.example.twitturin.search.data.model.SearchResponse
import com.example.twitturin.search.data.model.SearchUser
import com.example.twitturin.search.data.network.SearchApi
import com.example.twitturin.search.data.repository.SearchRepository
import retrofit2.Response

class SearchRepositoryImpl(
    private val searchApi: SearchApi
) : SearchRepository {

        override suspend fun searchNews(keyword : String): Response<SearchResponse> = searchApi.searchNews(keyword)

}