package com.example.twitturin.ui.sealeds

import com.example.twitturin.model.data.tweets.Tweet

sealed class SearchResource(){
    data class Success(val data : Tweet): SearchResource()
    class Error(message: String?) : SearchResource()
    class Loading : SearchResource()
}