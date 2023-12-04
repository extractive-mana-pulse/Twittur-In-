package com.example.twitturin.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.api.Api
import com.example.twitturin.model.data.users.UsersItem
import com.example.twitturin.ui.sealeds.DeleteResult
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ProfileViewModel: ViewModel() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val apiService: Api = retrofit.create(Api::class.java)

    private val _deleteResult = MutableLiveData<DeleteResult>()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    fun deleteUser(userId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteUser(userId, "Bearer $token")
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

    private val _userData = MutableLiveData<Response<List<UsersItem>>>()
    val userData: LiveData<Response<List<UsersItem>>> = _userData

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            try {
                val userData = apiService.getLoggedInUserData(userId)
                _userData.value = userData
            } catch (e: Exception) {
                e.message
            }
        }
    }
}
