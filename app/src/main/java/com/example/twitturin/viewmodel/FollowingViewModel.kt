package com.example.twitturin.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.network.FollowApi
import com.example.twitturin.ui.sealeds.DeleteFollow
import com.example.twitturin.ui.sealeds.FollowResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(private val api : FollowApi) : ViewModel() {


    /** use SingeLiveEvent instead MutableLiveData. cause in case when you use 2nd option you will receive a message toast or whatever
     * you have there multiple times. Single Live Event show this message only one time*/
    private val _followResult = SingleLiveEvent<FollowResult>()
    val followResult: LiveData<FollowResult> = _followResult

    fun followUsers(id : String, token: String) {
        api.followUser(id, token).enqueue(object : Callback<User> {
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

    private val _deleteFollow = SingleLiveEvent<DeleteFollow>()
    val deleteFollowResult: LiveData<DeleteFollow> = _deleteFollow

    fun deleteFollow(id : String, token: String) {
        api.deleteFollow(id, token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    _deleteFollow.value = DeleteFollow.Success
                } else {
                    _deleteFollow.value = DeleteFollow.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _deleteFollow.value = DeleteFollow.Error("Failed to unfollow user")
            }
        })
    }
}