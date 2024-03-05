package com.example.twitturin.search.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.search.domain.repository.SearchRepository
import com.example.twitturin.search.sealed.SearchResource
import com.example.twitturin.tweet.model.data.Tweet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val repository: SearchRepository
) : ViewModel() {

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