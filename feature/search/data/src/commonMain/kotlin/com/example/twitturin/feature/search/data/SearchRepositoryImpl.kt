package com.example.twitturin.feature.search.data

import com.example.twitturin.core.data.network.get
import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.core.domain.util.map
import com.example.twitturin.feature.search.domain.SearchRepository
import com.example.twitturin.feature.search.domain.SearchUser
import io.ktor.client.HttpClient

class SearchRepositoryImpl(
    private val httpClient: HttpClient,
) : SearchRepository {

    override suspend fun searchUsers(query: String): Result<List<SearchUser>, DataError.Network> {
        return httpClient.get<SearchResponseDto>(
            route = "search",
            queryParameters = mapOf("keyword" to query),
        ).map { dto -> dto.users.map { it.toSearchUser() } }
    }
}
