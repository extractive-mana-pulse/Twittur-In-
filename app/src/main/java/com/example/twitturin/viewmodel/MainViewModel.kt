package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.profile.sealed.UsersResult
import com.example.twitturin.tweet.model.data.Tweet
import com.example.twitturin.search.sealed.SearchResource
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel (
    private val repository: Repository
): ViewModel() {

    private val _usersResult = MutableLiveData<UsersResult>()
    val usersResult: LiveData<UsersResult> get() = _usersResult

    fun getAllUsers() {
        viewModelScope.launch {
            try {
                val response = repository.getAllUsers()
                if (response.isSuccessful) {
                    _usersResult.value = UsersResult.Success(response.body() ?: emptyList())
                } else {
                    _usersResult.value = UsersResult.Error("Error fetching users: ${response.code()}")
                }
            } catch (e: Exception) {
                _usersResult.value = UsersResult.Error("Exception: ${e.message}")
            }
        }
    }

    val searchNews: MutableLiveData<SearchResource> = MutableLiveData()

    fun searchString(searchQuery: Tweet) = viewModelScope.launch {
        searchNews.postValue(SearchResource.Loading)
        val response = repository.searchNews(searchQuery)
        searchNews.postValue(handleSearchNews(response))
    }

    private fun handleSearchNews(response: Response<Tweet>): SearchResource {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return SearchResource.Success(resultResponse)
            }
        }
        return SearchResource.Error(response.message())
    }
}