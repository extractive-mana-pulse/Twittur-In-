package com.example.twitturin.auth.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.domain.repository.AuthRepository
import com.example.twitturin.auth.presentation.model.data.Login
import com.example.twitturin.auth.presentation.model.data.User
import com.example.twitturin.auth.presentation.sealed.SignIn
import com.example.twitturin.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository : AuthRepository
) : ViewModel() {

    private val _signInResult = SingleLiveEvent<SignIn>()
    val signInResult: SingleLiveEvent<SignIn> = _signInResult

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    fun signIn(username: String, password: String) {

        val request = Login(username, password)

        repository.signInUser(request).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val token = signInResponse?.token
                    val userId = signInResponse?.id
                    _token.value = token!!
                    _userId.value = userId!!
                    _signInResult.value = signInResponse.let { SignIn.Success(it) }
                } else {
                    _signInResult.value = SignIn.Error("User doesn't exist")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _signInResult.value = SignIn.Error("Network error")
            }
        })
    }
}
