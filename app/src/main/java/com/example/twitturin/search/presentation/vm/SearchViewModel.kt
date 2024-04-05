package com.example.twitturin.search.presentation.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.search.data.model.SearchResponse
import com.example.twitturin.search.data.model.SearchUser
import com.example.twitturin.search.data.repository.SearchRepository
import com.example.twitturin.search.presentation.sealed.SearchResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val repository: SearchRepository
) : ViewModel() {

    val searchNews: MutableLiveData<SearchResource<SearchResponse>> = MutableLiveData()

    fun searchString(keyword : String) = viewModelScope.launch {
        searchNews.postValue(SearchResource.Loading())
        val response = repository.searchNews(keyword)
        searchNews.postValue(handleSearchNews(response))
    }

    private fun handleSearchNews(response: Response<SearchResponse>): SearchResource<SearchResponse> {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return SearchResource.Success(resultResponse)
            }
        }
        return SearchResource.Error(response.message())
    }
}