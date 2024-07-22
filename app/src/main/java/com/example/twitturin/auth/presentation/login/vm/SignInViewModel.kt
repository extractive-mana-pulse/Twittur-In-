package com.example.twitturin.auth.presentation.login.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitturin.auth.data.remote.repository.AuthRepository
import com.example.twitturin.auth.domain.model.AuthUser
import com.example.twitturin.auth.domain.model.Login
import com.example.twitturin.auth.presentation.login.sealed.SignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository : AuthRepository) : ViewModel() {

    private val _signIn = MutableStateFlow<SignIn>(SignIn.Loading)
    val signInResponse = _signIn.asStateFlow()

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    fun signIn(username: String, password: String) {

        val request = Login(username, password)

        repository.signInUser(request).enqueue(object : Callback<AuthUser> {

            override fun onResponse(call: Call<AuthUser>, response: Response<AuthUser>) {

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val token = signInResponse?.token
                    val userId = signInResponse?.id
                    _token.value = token!!
                    _userId.value = userId!!
                    _signIn.value = signInResponse.let { SignIn.Success(it) }
                } else {
                    _signIn.value = SignIn.Error("User doesn't exist")
                }
            }

            override fun onFailure(call: Call<AuthUser>, t: Throwable) {
                _signIn.value = SignIn.Error("Network error")
            }
        })
    }
}
