package com.example.twitturin.search.presentation.sealed

sealed class SearchResource<T> {
    class Initial<T> : SearchResource<T>()
    class Loading<T> : SearchResource<T>()
    data class Success<T>(val data: T) : SearchResource<T>()
    data class Error<T>(val message: String) : SearchResource<T>()
}