package com.example.twitturin.search.domain.repositoryImpl

import com.example.twitturin.auth.model.data.Head
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.search.domain.repository.SearchRepository
import com.example.twitturin.search.presentation.model.network.SearchApi
import retrofit2.Response

class SearchRepositoryImpl(
    private val searchApi: SearchApi
) : SearchRepository {

    override suspend fun searchNews(keyword : String): Response<Head> = searchApi.searchNews(keyword)

}