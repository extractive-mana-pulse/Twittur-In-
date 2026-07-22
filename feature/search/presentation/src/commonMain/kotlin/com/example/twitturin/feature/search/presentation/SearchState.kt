package com.example.twitturin.feature.search.presentation

import com.example.twitturin.feature.search.domain.SearchUser

data class SearchState(
    val query: String = "",
    val results: List<SearchUserUi> = emptyList(),
    /** Everyone on the platform (`GET users`) — shown before any query is typed. */
    val suggestions: List<SearchUserUi> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
)

/** Presentation model for a search result row (display-ready, no nullable display fields). */
data class SearchUserUi(
    val id: String,
    val username: String,
    val fullName: String,
    val bio: String,
    val profilePicture: String?,
)

fun SearchUser.toSearchUserUi(): SearchUserUi = SearchUserUi(
    id = id,
    username = username,
    fullName = fullName ?: "Twittur User",
    bio = bio.orEmpty(),
    profilePicture = profilePicture,
)
