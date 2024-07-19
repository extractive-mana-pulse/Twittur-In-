package com.example.twitturin.follow.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.event.SingleLiveEvent
import com.example.twitturin.follow.data.remote.repository.FollowRepository
import com.example.twitturin.follow.domain.model.FollowUser
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.following.sealed.UnFollow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(private val repository: FollowRepository) : ViewModel() {

    var followersList: MutableLiveData<Response<List<FollowUser>>> = MutableLiveData()
    fun getFollowers(userId : String) {

        viewModelScope.launch {
            val response = repository.getListOfFollowers(userId)
            followersList.value = response
        }
    }

    var followingList: MutableLiveData<Response<List<FollowUser>>> = MutableLiveData()

    fun getFollowing(userId : String) {
        viewModelScope.launch {
            val response = repository.getListOfFollowing(userId)
            followingList.value = response

        }
    }

    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun triggerSharedFlow(value : String){
        viewModelScope.launch {
            _sharedFlow.emit(value)
        }
    }

    private val _follow =
        SingleLiveEvent<Follow>()
    val follow: LiveData<Follow> = _follow

    fun followUsers(id : String, token: String) {

        repository.followUser(id, token).enqueue(object : Callback<FollowUser> {

            override fun onResponse(call: Call<FollowUser>, response: Response<FollowUser>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _follow.value = responseBody?.let { Follow.Success(it) }
                    triggerSharedFlow(value = responseBody?.username.toString())

                } else {
                    _follow.value = Follow.Error(response.message().toString())
                }
            }
            override fun onFailure(call: Call<FollowUser>, t: Throwable) {
                _follow.value = Follow.Error("Failed to follow user")
            }
        })
    }

    private val _unFollow =
        SingleLiveEvent<UnFollow>()
    val unFollow: LiveData<UnFollow> = _unFollow

    fun unFollowUser(id : String, token: String) {

        repository.unFollowUser(id, token).enqueue(object : Callback<FollowUser> {

            override fun onResponse(call: Call<FollowUser>, response: Response<FollowUser>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _unFollow.value = responseBody?.let { UnFollow.Success(it) }
                    triggerSharedFlow(value = responseBody?.username.toString())
                } else {
                    _unFollow.value = UnFollow.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<FollowUser>, t: Throwable) {
                _unFollow.value = UnFollow.Error("Failed to unfollow user")
            }
        })
    }
}