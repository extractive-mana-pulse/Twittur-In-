package com.example.twitturin.feature.search.domain

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.Result

/** Contract for searching users. Implemented in :feature:search:data over Ktor. */
interface SearchRepository {
    suspend fun searchUsers(query: String): Result<List<SearchUser>, DataError.Network>

    /** Everyone on the platform (`GET users`) — shown as suggestions before a query is typed. */
    suspend fun getAllUsers(): Result<List<SearchUser>, DataError.Network>
}
