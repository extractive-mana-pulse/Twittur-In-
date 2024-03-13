package com.example.twitturin.search.sealed

sealed class SearchResource<T>(val data : T? = null, val message : String? =  null) {

    class Success<T>(data: T) : SearchResource<T>(data)

    class Error<T>(message: String?, data: T? = null) : SearchResource<T>(data, message)

    class Loading<T> : SearchResource<T>()
}