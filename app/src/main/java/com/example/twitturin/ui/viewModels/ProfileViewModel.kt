package com.example.twitturin.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.model.api.Api
import com.example.twitturin.ui.sealeds.DeleteResult
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileViewModel: ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(Api::class.java)

    private val _deleteResult = MutableLiveData<DeleteResult>()
    val deleteResult: LiveData<DeleteResult> = _deleteResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun deleteUser(userId: String, token: String) {

        viewModelScope.launch {
            try {
                val response = userService.deleteUser(userId, "Bearer $token")
                if (response.isSuccessful) {
                    _token.value = token
                    _deleteResult.value = DeleteResult.Success
                } else {
                    _deleteResult.value = DeleteResult.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteResult.value = DeleteResult.Error("An error occurred: ${e.message}")
            }
        }
    }
}