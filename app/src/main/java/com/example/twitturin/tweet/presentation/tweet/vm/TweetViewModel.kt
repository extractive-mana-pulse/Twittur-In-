package com.example.twitturin.tweet.presentation.tweet.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.core.event.SingleLiveEvent
import com.example.twitturin.core.extensions.beGone
import com.example.twitturin.detail.presentation.sealed.PostReply
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.tweet.data.remote.repository.TweetRepository
import com.example.twitturin.tweet.domain.model.ReplyContent
import com.example.twitturin.tweet.domain.model.Tweet
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TweetViewModel @Inject constructor(private val repository: TweetRepository) : ViewModel() {

    private val responseTweets =  MutableStateFlow<Response<List<Tweet>>?>(null)
    val responseOfTweet: StateFlow<Response<List<Tweet>>?> = responseTweets.asStateFlow()

    fun getTweet(shimmerLayout: ShimmerFrameLayout) {
        shimmerLayout.startShimmer()

        viewModelScope.launch {
            val response = repository.getTweet()
            responseTweets.value = response
            shimmerLayout.stopShimmer()
            shimmerLayout.beGone()
        }
    }

    var userTweets: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getUserTweet(userId : String) {
        viewModelScope.launch {
            val response = repository.getPostsByUser(userId)
            userTweets.value = response
        }
    }

    private val _repliesOfPosts = MutableStateFlow<Response<List<Tweet>>?>(null)
    val repliesOfPosts: StateFlow<Response<List<Tweet>>?> = _repliesOfPosts

    fun getRepliesOfPost(tweetId: String) {
        viewModelScope.launch {
            val response = repository.getRepliesOfPost(tweetId)
            _repliesOfPosts.value = response
        }
    }


    var likedPosts: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getLikedPosts(userId : String) {
        viewModelScope.launch {
            val response = repository.getListOfLikedPosts(userId)
            likedPosts.value = response
        }
    }

    private val _deleteTweetResult = SingleLiveEvent<TweetDelete>()
    val deleteTweetResult: LiveData<TweetDelete> = _deleteTweetResult

    fun deleteTweet(tweetId: String, token : String) {
        viewModelScope.launch {
            try {
                val response = repository.deleteTweet(tweetId, token)
                if (response.isSuccessful) {
                    _deleteTweetResult.value = TweetDelete.Success
                } else {
                    _deleteTweetResult.value = TweetDelete.Error(response.code().toString())
                }
            } catch (e: Exception) {
                _deleteTweetResult.value = TweetDelete.Error("An error occurred: ${e.message}")
            }
        }
    }

    private val _postReply = MutableStateFlow<PostReply>(PostReply.Loading)
    val postReplyResult = _postReply.asStateFlow()

    fun postReply(content: String, tweetId: String, authToken: String) {
        val reply = ReplyContent(content)
        val replyRequest = repository.postReply(reply, tweetId, authToken)

        replyRequest.enqueue(object : Callback<ReplyContent> {
            override fun onResponse(call: Call<ReplyContent>, response: Response<ReplyContent>) {

                if (response.isSuccessful) {
                    val postReply = response.body()
                    _postReply.value = postReply?.let { PostReply.Success(it) }!!
                } else {
                    _postReply.value = PostReply.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<ReplyContent>, t: Throwable) {
                _postReply.value = PostReply.Error("Network error")
            }
        })
    }
}