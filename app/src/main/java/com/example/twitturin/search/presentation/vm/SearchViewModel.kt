package com.example.twitturin.search.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.search.data.remote.repository.SearchRepository
import com.example.twitturin.search.domain.model.SearchResponse
import com.example.twitturin.search.presentation.sealed.SearchResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _searchUser = MutableStateFlow<SearchResource<SearchResponse>>(SearchResource.Loading())
    val searchUser: StateFlow<SearchResource<SearchResponse>> = _searchUser.asStateFlow()

    fun searchUser(keyword: String) = viewModelScope.launch {
        _searchUser.value = SearchResource.Loading()
        val response = repository.searchUser(keyword)
        _searchUser.value = handleSearchResponse(response)
    }

    private fun handleSearchResponse(response: Response<SearchResponse>): SearchResource<SearchResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { result ->
                SearchResource.Success(result)
            } ?: SearchResource.Error("No data found.")
        } else {
            SearchResource.Error(response.message())
        }
    }
}