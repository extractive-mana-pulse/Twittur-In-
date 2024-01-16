package com.example.twitturin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.BuildConfig
import com.example.twitturin.model.data.publicTweet.TweetContent
import com.example.twitturin.model.data.replyToTweet.ReplyContent
import com.example.twitturin.model.data.tweets.Tweet
import com.example.twitturin.model.data.users.User
import com.example.twitturin.model.network.Api
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.ui.sealeds.DeleteResult
import com.example.twitturin.ui.sealeds.PostReply
import com.example.twitturin.ui.sealeds.PostTweet
import com.example.twitturin.ui.sealeds.SearchResource
import com.example.twitturin.viewmodel.event.SingleLiveEvent
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
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val tweetApi: Api = retrofit.create(Api::class.java)
    private val _postTweet = SingleLiveEvent<PostTweet>()
    val postTweetResult: LiveData<PostTweet> = _postTweet

    fun postTheTweet(content: String, authToken: String) {
        val request = TweetContent(content)
        val authRequest = tweetApi.postTweet(request, authToken)

        authRequest.enqueue(object : Callback<TweetContent> {
            override fun onResponse(call: Call<TweetContent>, response: Response<TweetContent>) {

                if (response.isSuccessful) {
                    val postTweet = response.body()
                    _postTweet.value = postTweet?.let { PostTweet.Success(it) }
                } else {
                    _postTweet.value = PostTweet.Error(response.body().toString())
                }
            }

            override fun onFailure(call: Call<TweetContent>, t: Throwable) {
                _postTweet.value = PostTweet.Error("Network error")
            }
        })
    }

    private val _postReply = SingleLiveEvent<PostReply>()
    val postReplyResult: LiveData<PostReply> = _postReply

    fun postReply(content: String, tweetId: String, authToken: String) {
        val reply = ReplyContent(content)
        val replyRequest = tweetApi.postReply(reply, tweetId, authToken)

        replyRequest.enqueue(object : Callback<ReplyContent> {
            override fun onResponse(call: Call<ReplyContent>, response: Response<ReplyContent>) {

                if (response.isSuccessful) {
                    val postReply = response.body()
                    _postReply.value = postReply?.let { PostReply.Success(it) }
                } else {
                    _postReply.value = PostReply.Error(response.code().toString())
                    Log.d("body", response.body().toString())
                    Log.d("error body", response.errorBody().toString())
                    Log.d("message", response.message().toString())
                    Log.d("code", response.code().toString())
                }
            }

            override fun onFailure(call: Call<ReplyContent>, t: Throwable) {
                _postReply.value = PostReply.Error("Network error")
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

    var userTweets: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getUserTweet(userId : String) {
        viewModelScope.launch {
            val response = repository.getUserTweets(userId)
            userTweets.value = response
        }
    }

    var followersList: MutableLiveData<Response<List<User>>> = MutableLiveData()
    fun getFollowers(userId : String) {
        viewModelScope.launch {
            val response = repository.getFollowersList(userId)
            followersList.value = response
        }
    }

    var followingList: MutableLiveData<Response<List<User>>> = MutableLiveData()
    fun getFollowing(userId : String) {
        viewModelScope.launch {
            val response = repository.getFollowingList(userId)
            followingList.value = response
        }
    }

    var likedPosts: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getLikedPosts(userId : String) {
        viewModelScope.launch {
            val response = repository.getListOfLikedPosts(userId)
            likedPosts.value = response
        }
    }

    var repliesOfPosts: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getRepliesOfPost(tweetId : String) {
        viewModelScope.launch {
            val response = repository.getRepliesOfTweet(tweetId)
            repliesOfPosts.value = response
        }
    }

    val searchNews: MutableLiveData<SearchResource> = MutableLiveData()

    fun searchString(searchQuery: Tweet) = viewModelScope.launch {
        searchNews.postValue(SearchResource.Loading())
        val response = repository.searchNews(searchQuery)
        searchNews.postValue(handleSearchNews(response))
    }

    private fun handleSearchNews(response: Response<Tweet>): SearchResource{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return SearchResource.Success(resultResponse)
            }
        }
        return SearchResource.Error(response.message())
    }
}