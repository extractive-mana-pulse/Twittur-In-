package com.example.twitturin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.data.editUser.EditProfile
import com.example.twitturin.model.network.Api
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.EditUserResult
import com.example.twitturin.ui.sealeds.UserCredentialsResult
import com.example.twitturin.ui.sealeds.UserTweetsResult
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
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val apiService: Api = retrofit.create(Api::class.java)

    private val _deleteResult = MutableLiveData<DeleteResult>()
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
        val authRequest = apiService.editUser(request, userId, "Bearer $token")
        authRequest.enqueue(object : Callback<EditProfile> {
            override fun onResponse(call: Call<EditProfile>, response: Response<EditProfile>) {
                if (response.isSuccessful) {
                    val editProfile = response.body()
                    if (editProfile != null) {
                        _editUserResult.value = EditUserResult.Success(editProfile)
                        Log.d("response body", response.body().toString())
                        Log.d("response code", response.code().toString())
                        Log.d("response message", response.message().toString())
                    }
                } else {
                    _editUserResult.value = EditUserResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<EditProfile>, t: Throwable) {
                _editUserResult.value = EditUserResult.Error(t.message ?: "Unknown error")
            }
        })
    }

    private val _data = MutableLiveData<UserTweetsResult>()
    val data: LiveData<UserTweetsResult> = _data

//    fun getPostsFromUser(userId: String){
//        val call = apiService.getPostsByUser(userId)
//        call.enqueue(object : Callback<List<Tweet>> {
//            override fun onResponse(call: Call<List<Tweet>>, response: Response<List<Tweet>>) {
//                if (response.isSuccessful) {
//                    val tweets = response.body()
//                    _data.value = tweets?.let { UserTweetsResult.Success(it) }
//                } else {
//                    _data.value = UserTweetsResult.Failure(response.code().toString())
//                    Log.d("body", response.body().toString())
//                    Log.d("code", response.code().toString())
//                    Log.d("message", response.message().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<List<Tweet>>, t: Throwable) {
//                t.message
//            }
//        })
//    }


    fun followUser(userId: String, token: String){
        val call = apiService.followUser(userId, token)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful){
                    Log.d("response body", response.body().toString())
                    Log.d("response code", response.code().toString())
                }
                else {
                    Log.d("response body error", response.body().toString())
                    Log.d("response code error", response.code().toString())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                t.message
            }
        })
    }
}
