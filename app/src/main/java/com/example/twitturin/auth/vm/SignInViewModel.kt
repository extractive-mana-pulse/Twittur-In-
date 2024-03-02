package com.example.twitturin.auth.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import com.example.twitturin.model.network.Api
import com.example.twitturin.auth.data.Login
import com.example.twitturin.model.data.users.User
import com.example.twitturin.ui.sealeds.SignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val api : Api) : ViewModel() {

    private val _signInResult = SingleLiveEvent<SignInResult>()
    val signInResult: SingleLiveEvent<SignInResult> = _signInResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    fun signIn(username: String, password: String) {

        val request = Login(username, password)

        api.signInUser(request).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val token = signInResponse?.token
                    val userId = signInResponse?.id
                    _token.value = token!!
                    _userId.value = userId!!
                    _signInResult.value = signInResponse.let { SignInResult.Success(it) }
                } else {
                    _signInResult.value = SignInResult.Error("User doesn't exist")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _signInResult.value = SignInResult.Error("Network error")
            }
        })
    }
}