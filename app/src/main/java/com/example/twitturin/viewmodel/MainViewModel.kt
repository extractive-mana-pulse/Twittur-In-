package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.SingleLiveEvent
import com.example.twitturin.model.api.Api
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.registration.SignUp
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.ui.sealeds.SignUpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainViewModel(private val repository: Repository): ViewModel() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    val userData = MutableLiveData<User>()

    suspend fun fetchUserData(userId: String) {
        withContext(Dispatchers.IO) {
            val response = repository.getLoggedInUserData(userId)
            if (response.isSuccessful) {
                val userList = response.body()
                if (!userList.isNullOrEmpty()) {
                    val user = userList[0]
                    userData.postValue(user)
                }
            } else {
                // Handle error
            }
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val signUpApi: Api = retrofit.create(Api::class.java)

    private val _signUpResult = MutableLiveData<SignUpResult>()
    val signUpResult: LiveData<SignUpResult> = _signUpResult

    fun signUp(username: String, studentId: String, major: String, email: String, password: String, kind: String) {
        val request = SignUp(username, studentId, major, email, password, kind)
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

    private val _postTweet = SingleLiveEvent<PostTweet>()

    val postTweetResult: LiveData<PostTweet> = _postTweet

    fun postTheTweet(content: String, authToken: String) {
        val request = TweetContent(content)

        val authRequest = tweetApi.postTweet(request, "Bearer $authToken")
        authRequest.enqueue(object : Callback<TweetContent> {
            override fun onResponse(call: Call<TweetContent>, response: Response<TweetContent>) {
                if (response.isSuccessful) {
                    val postTweet = response.body()
                    _postTweet.value = postTweet?.let { PostTweet.Success(it) }
                } else {
                    _postTweet.value = PostTweet.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<TweetContent>, t: Throwable) {
                _postTweet.value = PostTweet.Error("Network error")
            }
        })
    }

    var responseTweets: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getTweet() {
        viewModelScope.launch {
            val response = repository.getTweets()
            responseTweets.value = response
        }
    }
}