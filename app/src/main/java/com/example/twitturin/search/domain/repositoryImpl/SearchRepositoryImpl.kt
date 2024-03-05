package com.example.twitturin.search.domain.repositoryImpl

import com.example.twitturin.search.domain.repository.SearchRepository
import com.example.twitturin.search.model.network.SearchApi
import com.example.twitturin.tweet.model.data.Tweet
import retrofit2.Response

class SearchRepositoryImpl(
    private val searchApi: SearchApi
) : SearchRepository {

    override suspend fun searchNews(keyword: Tweet): Response<Tweet> = searchApi.searchNews(keyword)

}