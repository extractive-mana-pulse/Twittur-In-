package com.example.twitturin.follow.vm


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.follow.model.domain.repository.FollowRepository
import com.example.twitturin.auth.model.data.User
import com.example.twitturin.follow.sealed.UnFollow
import com.example.twitturin.follow.sealed.FollowResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val repository: FollowRepository
) : ViewModel() {

    var followersList: MutableLiveData<Response<List<User>>> = MutableLiveData()
    fun getFollowers(userId : String) {

        viewModelScope.launch {
            val response = repository.getListOfFollowers(userId)
            followersList.value = response
        }
    }

    var followingList: MutableLiveData<Response<List<User>>> = MutableLiveData()
    fun getFollowing(userId : String) {
        viewModelScope.launch {
            val response = repository.getListOfFollowing(userId)
            followingList.value = response

        }
    }

    /** use SingeLiveEvent instead MutableLiveData. cause in case when you use 2nd option you will receive a message toast or whatever
     * you have there multiple times. Single Live Event show this message only one time*/
    private val _followResult = SingleLiveEvent<FollowResult>()
    val followResult: LiveData<FollowResult> = _followResult

    fun followUsers(id : String, token: String) {
        repository.followUser(id, token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _followResult.value = responseBody?.let { FollowResult.Success(it) }
                } else {
                    _followResult.value = FollowResult.Error(response.message().toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _followResult.value = FollowResult.Error("Failed to follow user")
            }
        })
    }


    /** this code made for un follow single user when user press unfollow button.*/

    private val _deleteFollow = SingleLiveEvent<UnFollow>()
    val deleteFollowResult: LiveData<UnFollow> = _deleteFollow

    fun unFollow(id : String, token: String) {
        repository.deleteFollow(id, token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _deleteFollow.value = responseBody?.let { UnFollow.Success(it) }
                } else {
                    _deleteFollow.value = UnFollow.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _deleteFollow.value = UnFollow.Error("Failed to unfollow user")
            }
        })
    }
}