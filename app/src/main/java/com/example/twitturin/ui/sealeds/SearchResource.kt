package com.example.twitturin.ui.sealeds

import com.example.twitturin.tweet.model.data.Tweet

sealed class SearchResource{
    data class Success(val data : Tweet): SearchResource()
    class Error(val message: String?) : SearchResource()
    data object Loading : SearchResource()
}