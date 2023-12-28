package com.example.twitturin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.BuildConfig
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.network.Api
import com.example.twitturin.model.network.FollowApi
import com.example.twitturin.ui.sealeds.DeleteFollow
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.ui.sealeds.FollowResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class   FollowUserViewModel : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: FollowApi = retrofit.create(FollowApi::class.java)

    private val _followResult = MutableLiveData<FollowResult>()
    val followResult: LiveData<FollowResult> = _followResult

    fun followUsers(id : String, token: String) {
        api.followUser(id, token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    _followResult.value = FollowResult.Success
                } else {
                    _followResult.value = FollowResult.Error(response.code().toString())
                    Log.d("error code", response.code().toString())
                    Log.d("error body", response.body().toString())
                    Log.d("message_ body", response.message().toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _followResult.value = FollowResult.Error("Failed to follow user")
            }
        })
    }

    private val _deleteFollow = MutableLiveData<DeleteFollow>()
    val deleteFollowResult: LiveData<DeleteFollow> = _deleteFollow

    fun deleteFollow(id : String, token: String) {
        api.deleteFollow(id, token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    _deleteFollow.value = DeleteFollow.Success
                } else {
                    _deleteFollow.value = DeleteFollow.Error(response.code().toString())
                    Log.d("error code", response.code().toString())
                    Log.d("error body", response.body().toString())
                    Log.d("message_ body", response.message().toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _deleteFollow.value = DeleteFollow.Error("Failed to follow user")
            }
        })
    }
}