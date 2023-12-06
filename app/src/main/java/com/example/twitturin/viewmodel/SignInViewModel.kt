package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.SingleLiveEvent
import com.example.twitturin.model.api.Api
import com.example.twitturin.model.data.registration.Login
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.sealeds.SignInResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInViewModel : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val signInApi: Api = retrofit.create(Api::class.java)

    private val _signInResult = SingleLiveEvent<SignInResult>()
    val signInResult: SingleLiveEvent<SignInResult> = _signInResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    fun signIn(username: String, password: String) {

        val request = Login(username, password)

        signInApi.signInUser(request).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val token = signInResponse?.token
                    val userId = signInResponse?.id
                    _token.value = token!!
                    _userId.value = userId!!
                    _signInResult.value = signInResponse.let { SignInResult.Success(it) }

                } else {

                    _signInResult.value = SignInResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _signInResult.value = SignInResult.Error("Network error")
            }
        })
    }
}