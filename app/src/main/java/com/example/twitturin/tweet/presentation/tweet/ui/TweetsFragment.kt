package com.example.twitturin.tweet.presentation.tweet.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentTweetsBinding
import com.example.twitturin.manager.SessionManager
import com.example.twitturin.profile.presentation.util.snackbarError
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.adapters.TweetAdapter
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class TweetsFragment : Fragment() {

    @Inject lateinit var sessionManager: SessionManager
    private val tweetViewModel : TweetViewModel by viewModels()
    private val binding by lazy { FragmentTweetsBinding.inflate(layoutInflater) }
    private val userPostAdapter by lazy { TweetAdapter(tweetViewModel, viewLifecycleOwner) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {

        binding.apply {

            tweetViewModel.getUserTweet(sessionManager.getUserId()!!)
            rcView.adapter = userPostAdapter
            rcView.layoutManager = LinearLayoutManager(requireContext())
            rcView.addItemDecoration(DividerItemDecoration(rcView.context, DividerItemDecoration.VERTICAL))

            tweetViewModel.userTweets.observe(requireActivity()) { response ->
                if (response.isSuccessful) {
                    response.body()?.let { tweets ->

                        val tweetList: MutableList<Tweet> = tweets.toMutableList()
                        swipeToRefreshLayoutTweets.setOnRefreshListener {

                            tweetViewModel.getUserTweet(sessionManager.getUserId()!!)
                            tweetList.shuffle(Random(System.currentTimeMillis()))
                            swipeToRefreshLayoutTweets.isRefreshing = false
                            userPostAdapter.notifyDataSetChanged()

                        }

                        if (tweetList.isEmpty()) {

                            rcView.visibility = View.GONE
                            tweetsPageAnView.visibility = View.VISIBLE
                            lottieInfoTv.visibility = View.VISIBLE

                        } else {

                            rcView.visibility = View.VISIBLE
                            tweetsPageAnView.visibility = View.GONE
                            lottieInfoTv.visibility = View.GONE
                            userPostAdapter.differ.submitList(tweetList)
                            userPostAdapter.notifyDataSetChanged()

                        }
                    }

                } else {
                    tweetsRootLayout.snackbarError(
                        requireActivity().findViewById(R.id.tweets_root_layout),
                        error = response.message(),
                        ""){}
                }
            }
        }
    }
}