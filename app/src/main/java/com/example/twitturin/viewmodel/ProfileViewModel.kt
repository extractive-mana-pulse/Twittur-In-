package com.example.twitturin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.BuildConfig
import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.network.Api
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ProfileViewModel: ViewModel() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val apiService: Api = retrofit.create(Api::class.java)
    private val _deleteResult = SingleLiveEvent<DeleteResult>()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    fun deleteUser(userId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteUser(userId, token)
                if (response.isSuccessful) {
                    _deleteResult.value = DeleteResult.Success
                } else {
                    _deleteResult.value = DeleteResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteResult.value = DeleteResult.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _deleteTweetResult = SingleLiveEvent<DeleteResult>()
    val deleteTweetResult: LiveData<DeleteResult> = _deleteTweetResult

    fun deleteTweet(tweetId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteTweet(tweetId, token)
                if (response.isSuccessful) {
                    _deleteTweetResult.value = DeleteResult.Success
                } else {
                    _deleteTweetResult.value = DeleteResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteTweetResult.value = DeleteResult.Error("An error occurred: ${e.message}")
            }
        }
    }


    private val _getUserCredentials = MutableLiveData<UserCredentialsResult>()
    val getUserCredentials: LiveData<UserCredentialsResult> = _getUserCredentials

    fun getUserCredentials(userId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getLoggedInUserData(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    _getUserCredentials.value = user?.let { UserCredentialsResult.Success(it) }
                } else {
                    _getUserCredentials.value = UserCredentialsResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _getUserCredentials.value = UserCredentialsResult.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _editUserResult = MutableLiveData<EditUserResult>()
    val editUserResult: LiveData<EditUserResult> = _editUserResult

    fun editUser(
        fullName: String,
        username: String,
        email: String,
        bio: String,
        country: String,
        birthday: String,
        userId: String,
        token: String
    ) {
        val request =  EditProfile(fullName, username, email, bio, country, birthday)
        /*username, email, birthday, bio, country, fullName*/
        val authRequest = apiService.editUser(request, userId, token)
        authRequest.enqueue(object : Callback<EditProfile> {
            override fun onResponse(call: Call<EditProfile>, response: Response<EditProfile>) {
                if (response.isSuccessful) {
                    val editProfile = response.body()
                    if (editProfile != null) {
                        _editUserResult.value = EditUserResult.Success(editProfile)
                    }
                } else {
                    _editUserResult.value = EditUserResult.Error(response.code())
                }
            }

            override fun onFailure(call: Call<EditProfile>, t: Throwable) {
                _editUserResult.value = EditUserResult.Error(404)
            }
        })
    }
}
