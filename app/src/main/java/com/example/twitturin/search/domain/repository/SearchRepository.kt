package com.example.twitturin.search.domain.repository

import com.example.twitturin.tweet.model.data.Tweet
import retrofit2.Response

interface SearchRepository {

    suspend fun searchNews(keyword: Tweet) : Response<Tweet>
}