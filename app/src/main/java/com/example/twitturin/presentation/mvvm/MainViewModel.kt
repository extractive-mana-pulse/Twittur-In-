package com.example.twitturin.presentation.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.SessionManager
import com.example.twitturin.presentation.sealeds.SignInResult
import com.example.twitturin.presentation.sealeds.SignUpResult
import com.example.twitturin.presentation.api.Api
import com.example.twitturin.presentation.api.RetrofitInstance
import com.example.twitturin.presentation.data.registration.SignIn
import com.example.twitturin.presentation.data.registration.SignUp
import com.example.twitturin.presentation.data.publicTweet.TheTweet
import com.example.twitturin.presentation.data.tweets.ApiTweetsItem
import com.example.twitturin.presentation.data.users.UsersItem
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

    private val signUpApi: Api = retrofit.create(Api::class.java)

    private val _signUpResult = MutableLiveData<SignUpResult>()
    val signUpResult: LiveData<SignUpResult> = _signUpResult

    fun signUp(
        username: String,
        studentId: String,
        major: String,
        email: String,
        password: String,
        kind: String
    ) {
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

//    fun postTheTweet(content: String) {
//        val request = TheTweet(content)
//        tweetApi.postTweet(request).enqueue(object : Callback<TheTweet> {
//            override fun onResponse(call: Call<TheTweet>, response: Response<TheTweet>) {
//                if (response.isSuccessful) {
//                    val postTweet = response.body()
//                    _token.value = token.toString()
//                    _postTweet.value = postTweet?.let { PostTweet.Success(it) }
//                } else {
//                    _postTweet.value = PostTweet.Error(response.code().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<TheTweet>, t: Throwable) {
//                _postTweet.value = PostTweet.Error("Network error")
//            }
//        })
//    }

    fun postTheTweet(content: String, authToken: String) {
        val request = TheTweet(content)

        // Add the authorization header to the API request
        val authRequest = tweetApi.postTweet(request,"Bearer $authToken")

        authRequest.enqueue(object : Callback<TheTweet> {
            override fun onResponse(call: Call<TheTweet>, response: Response<TheTweet>) {
                if (response.isSuccessful) {
                    val postTweet = response.body()

                    _postTweet.value = postTweet?.let { PostTweet.Success(it) }
                } else {
                    _postTweet.value = PostTweet.Error(response.code().toString())
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

    private val apiService: Api = retrofit.create(Api::class.java)

    private val _data = MutableLiveData<Call<List<UsersItem>>>()
    val data: LiveData<Call<List<UsersItem>>> get() = _data

    fun fetchData(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getAuthUserData("Bearer $token")
                _data.value = response

            } catch (e: Exception) {
                Log.d("TAG", e.message.toString())
            }
        }
    }

    val usersItemLiveData = MutableLiveData<UsersItem?>()
    fun setDataToTextView(token: String) {

            val retrofitData = RetrofitInstance.api.getAuthUserData("Bearer $token")
            retrofitData.enqueue(object : Callback<List<UsersItem>?> {

                override fun onResponse(call: Call<List<UsersItem>?>, response: Response<List<UsersItem>?>) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val usersItem = responseBody?.get(0) // Assuming only one UsersItem is returned
                        usersItemLiveData.value = usersItem
                    }
                }

                override fun onFailure(call: Call<List<UsersItem>?>, t: Throwable) {
                    // Handle failure
                }
            })
        }


//    fun getTweetForAuthUser(username: String, password: String){
//        var user : ApiUsersItem? = null
//
//        val mainApi = retrofit.create(Api::class.java)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            mainApi.signInUser(SignIn(username, password))
//            mainApi.getTweets(user?.token ?: "")
//        }
//    }
}