package com.example.twitturin.presentation.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.presentation.sealeds.SignInResult
import com.example.twitturin.presentation.sealeds.SignUpResult
import com.example.twitturin.presentation.api.Api
import com.example.twitturin.presentation.data.SignIn
import com.example.twitturin.presentation.data.SignUp
import com.example.twitturin.presentation.data.TheTweet
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.sealeds.PostTweet
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

    private val signInApi: Api = retrofit.create(Api::class.java)

    private val _signInResult = MutableLiveData<SignInResult>()
    val signInResult: LiveData<SignInResult> = _signInResult

    fun signIn(studentId: String, password: String) {
        val request = SignIn(studentId, password)
        signInApi.signInUser(request).enqueue(object : Callback<SignIn> {
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

    private val signUpApi: Api = retrofit.create(Api::class.java)

    private val _signUpResult = MutableLiveData<SignUpResult>()
    val signUpResult: LiveData<SignUpResult> = _signUpResult

    fun signUp(
        username: String,
        studentId: String,
        major: String,
        email: String,
        password: String
    ) {
        val request = SignUp(username, studentId, major, email, password)
        signUpApi.signUpUser(request).enqueue(object : Callback<SignUp> {
            override fun onResponse(call: Call<SignUp>, response: Response<SignUp>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    _signUpResult.value = signUpResponse?.let { SignUpResult.Success(it) }
                } else {
                    _signUpResult.value = SignUpResult.Error(response.body().toString())
                }
            }

            override fun onFailure(call: Call<SignUp>, t: Throwable) {
                _signUpResult.value = SignUpResult.Error("Network error")
            }
        })
    }

    private val tweetApi: Api = retrofit.create(Api::class.java)

    private val _postTweet = MutableLiveData<PostTweet>()
    val postTweetResult: LiveData<PostTweet> = _postTweet

    fun postTheTweet(content: String) {
        val request = TheTweet(content)
        tweetApi.postTweet(request).enqueue(object : Callback<TheTweet> {
            override fun onResponse(call: Call<TheTweet>, response: Response<TheTweet>) {
                if (response.isSuccessful) {
                    val postTweet = response.body()
                    _postTweet.value = postTweet?.let { PostTweet.Success(it) }
                } else {
                    _postTweet.value = PostTweet.Error("Some error occur while posting the sheet")
                }
            }

            override fun onFailure(call: Call<TheTweet>, t: Throwable) {
                _postTweet.value = PostTweet.Error("Network error")
            }
        })
    }

    var responseTweets: MutableLiveData<Response<List<ApiTweetsItem>>> = MutableLiveData()
    fun getTweet() {
        viewModelScope.launch {
            val response = repository.getTweets()
            responseTweets.value = response
        }
    }
}