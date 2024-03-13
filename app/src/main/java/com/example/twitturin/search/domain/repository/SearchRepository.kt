package com.example.twitturin.search.domain.repository

import com.example.twitturin.auth.model.data.Head
import com.example.twitturin.auth.model.data.User
import retrofit2.Response

interface SearchRepository {
    suspend fun searchNews(keyword : String) : Response<Head>
}