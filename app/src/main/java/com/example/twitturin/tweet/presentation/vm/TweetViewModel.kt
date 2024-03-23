package com.example.twitturin.tweet.presentation.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitturin.event.SingleLiveEvent
import com.example.twitturin.tweet.domain.repository.TweetRepository
import com.example.twitturin.tweet.presentation.model.data.ReplyContent
import com.example.twitturin.tweet.presentation.model.data.Tweet
import com.example.twitturin.tweet.presentation.model.data.TweetContent
import com.example.twitturin.tweet.presentation.sealed.EditTweet
import com.example.twitturin.tweet.presentation.sealed.PostReply
import com.example.twitturin.tweet.presentation.sealed.PostTweet
import com.example.twitturin.tweet.presentation.sealed.TweetDelete
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TweetViewModel @Inject constructor(
    private val repository: TweetRepository
) : ViewModel() {

    var responseTweets: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getTweet(shimmerLayout: ShimmerFrameLayout) {
        shimmerLayout.startShimmer()

        viewModelScope.launch {
            val response = repository.getTweet()
            responseTweets.value = response
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }
    }

    var userTweets: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getUserTweet(userId : String) {
        viewModelScope.launch {
            val response = repository.getPostsByUser(userId)
            userTweets.value = response
        }
    }

    var repliesOfPosts: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getRepliesOfPost(tweetId : String) {
        viewModelScope.launch {
            val response = repository.getRepliesOfPost(tweetId)
            repliesOfPosts.value = response
        }
    }

    var likedPosts: MutableLiveData<Response<List<Tweet>>> = MutableLiveData()
    fun getLikedPosts(userId : String) {
        viewModelScope.launch {
            val response = repository.getListOfLikedPosts(userId)
            likedPosts.value = response
        }
    }

    private val _deleteTweetResult =
        SingleLiveEvent<TweetDelete>()
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

    private val _editTweetResult = MutableLiveData<EditTweet>()
    val editTweetResult: LiveData<EditTweet> = _editTweetResult

    fun editTweet(content: String, tweetId: String, token: String) {
        val request = TweetContent(content)
        val authRequest = repository.editTweet(tweetContent = request, tweetId = tweetId, token = token)
        authRequest.enqueue(object : Callback<TweetContent> {
            override fun onResponse(call: Call<TweetContent>, response: Response<TweetContent>) {
                if (response.isSuccessful) {
                    val editTweet = response.body()
                    if (editTweet != null) {
                        _editTweetResult.value = EditTweet.Success(request)
                    }
                } else {
                    _editTweetResult.value = EditTweet.Error(response.code())
                }
            }

            override fun onFailure(call: Call<TweetContent>, t: Throwable) {
                _editTweetResult.value = EditTweet.Error(404)
            }
        })
    }

    private val _postReply =
        SingleLiveEvent<PostReply>()
    val postReplyResult: LiveData<PostReply> = _postReply

    fun postReply(content: String, tweetId: String, authToken: String) {
        val reply = ReplyContent(content)
        val replyRequest = repository.postReply(reply, tweetId, authToken)

        replyRequest.enqueue(object : Callback<ReplyContent> {
            override fun onResponse(call: Call<ReplyContent>, response: Response<ReplyContent>) {

                if (response.isSuccessful) {
                    val postReply = response.body()
                    _postReply.value = postReply?.let { PostReply.Success(it) }
                } else {
                    _postReply.value = PostReply.Error(response.code().toString())
                }
            }

            override fun onFailure(call: Call<ReplyContent>, t: Throwable) {
                _postReply.value = PostReply.Error("Network error")
            }
        })
    }

    private val _postTweet =
        SingleLiveEvent<PostTweet>()
    val postTweetResult: LiveData<PostTweet> = _postTweet

    fun postTheTweet(content: String, authToken: String) {
        val request = TweetContent(content)
        val authRequest = repository.postTweet(request, authToken)

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
}