package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.SingleLiveEvent
import com.example.twitturin.model.api.Api
import com.example.twitturin.model.data.registration.SignIn
import com.example.twitturin.model.data.users.UsersItem
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.sealeds.SignInResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInViewModel(private val repository: Repository) : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val signInApi: Api = retrofit.create(Api::class.java)

    private val _signInResult = SingleLiveEvent<SignInResult>()
    val signInResult: SingleLiveEvent<SignInResult> = _signInResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun signIn(username: String, password: String) {

        val request = SignIn(username, password)

        signInApi.signInUser(request).enqueue(object : Callback<UsersItem> {

            override fun onResponse(call: Call<UsersItem>, response: Response<UsersItem>) {

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val token = signInResponse?.token
                    _token.value = token!!
                    _signInResult.value = signInResponse.let { SignInResult.Success(it) }

                } else {

                    _signInResult.value = SignInResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<UsersItem>, t: Throwable) {
                _signInResult.value = SignInResult.Error("Network error")
            }
        })
    }
}