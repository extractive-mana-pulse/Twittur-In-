package com.example.twitturin.presentation.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.SignInResult
import com.example.twitturin.presentation.api.Api
import com.example.twitturin.presentation.data.SignIn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(private val repository: Repository): ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi: Api = retrofit.create(Api::class.java)

    private val _signInResult = MutableLiveData<SignInResult>()
    val signInResult: LiveData<SignInResult> = _signInResult

    fun signIn(studentId: String, password: String) {
        val request = SignIn(studentId, password)
        authApi.authUser(request).enqueue(object : Callback<SignIn> {
            override fun onResponse(call: Call<SignIn>, response: Response<SignIn>) {
                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    _signInResult.value = signInResponse?.let { SignInResult.Success(it) }
                } else {
                    _signInResult.value = SignInResult.Error("Sign-in failed")
                }
            }

            override fun onFailure(call: Call<SignIn>, t: Throwable) {
                _signInResult.value = SignInResult.Error("Network error")
            }
        })
    }










//    var authResponse: MutableLiveData<Response<SignIn>> = MutableLiveData()
//    fun authUser(user:SignIn){
//        viewModelScope.launch {
//            val response = repository.authUser(user)
//            authResponse.value = response
//        }
//    }
}