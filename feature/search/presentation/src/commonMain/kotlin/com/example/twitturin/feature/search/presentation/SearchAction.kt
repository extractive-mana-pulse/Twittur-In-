package com.example.twitturin.feature.search.presentation

sealed interface SearchAction {
    data class OnQueryChange(val query: String) : SearchAction
    data class OnSearch(val query: String) : SearchAction
    data class OnUserClick(val userId: String) : SearchAction
}
