package com.example.twitturin.search.domain.repositoryImpl

import com.example.twitturin.search.domain.model.SearchResponse
import com.example.twitturin.search.data.remote.api.SearchApi
import com.example.twitturin.search.data.remote.repository.SearchRepository
import retrofit2.Response

class SearchRepositoryImpl(
    private val searchApi: SearchApi
) : SearchRepository {

    override suspend fun searchNews(keyword : String): Response<SearchResponse> = searchApi.searchNews(keyword)
}