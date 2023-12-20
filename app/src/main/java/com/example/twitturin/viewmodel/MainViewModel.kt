package com.example.twitturin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.viewmodel.event.SingleLiveEvent
import com.example.twitturin.model.network.Api
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.registration.SignUpStudent
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.ui.sealeds.SignUpStudentResult
import kotlinx.coroutines.launch
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

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitturin.onrender.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val signUpApi: Api = retrofit.create(Api::class.java)

    private val _signUpStudentResult = SingleLiveEvent<SignUpStudentResult>()
    val signUpStudentResult: LiveData<SignUpStudentResult> = _signUpStudentResult

    fun signUp(username: String, studentId: String, major: String, email: String, birthday : String, password: String, kind: String) {
        val request = SignUpStudent(username, studentId, major, email, birthday, password, kind)
        signUpApi.signUpStudent(request).enqueue(object : Callback<SignUpStudent> {
            override fun onResponse(call: Call<SignUpStudent>, response: Response<SignUpStudent>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    _signUpStudentResult.value = signUpResponse?.let { SignUpStudentResult.Success(it) }
                } else {
                    _signUpStudentResult.value = SignUpStudentResult.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<SignUpStudent>, t: Throwable) {
                _signUpStudentResult.value = SignUpStudentResult.Error("Network error")
            }
        })
    }

    private val tweetApi: Api = retrofit.create(Api::class.java)
    private val _postTweet =
        SingleLiveEvent<PostTweet>()
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

    private var userTweets: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getUserTweet(userId : String) {
        viewModelScope.launch {
            val response = repository.getUserTweets(userId)
            userTweets.value = response
        }
    }
}