package com.example.twitturin.detail.presentation.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.detail.data.remote.repository.ListOfLikesRepository
import com.example.twitturin.detail.domain.model.UserLikesAPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ListOfLikesViewModel @Inject constructor(private val repository: ListOfLikesRepository): ViewModel() {

    var listOfLikesToThePosts: MutableLiveData<Response<List<UserLikesAPost>>> = MutableLiveData()

    fun getListOfUsersLikesAPost(tweetId : String) {
        viewModelScope.launch {
            val response = repository.usersWhoLikesAPost(tweetId)
            listOfLikesToThePosts.value = response
        }
    }
}