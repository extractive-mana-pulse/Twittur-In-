package com.example.twitturin.tweet.presentation.fragments

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
import com.example.twitturin.tweet.presentation.adapters.TweetAdapter
import com.example.twitturin.tweet.presentation.vm.TweetViewModel
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

        val userId = sessionManager.getUserId()
        tweetViewModel.getUserTweet(userId!!)
        binding.rcView.adapter = userPostAdapter
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.addItemDecoration(DividerItemDecoration(binding.rcView.context, DividerItemDecoration.VERTICAL))

        tweetViewModel.userTweets.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.let { tweets ->

                    val tweetList: MutableList<Tweet> = tweets.toMutableList()
                    binding.swipeToRefreshLayoutTweets.setOnRefreshListener {

                        tweetViewModel.getUserTweet(userId)
                        userPostAdapter.notifyDataSetChanged()
                        tweetList.shuffle(Random(System.currentTimeMillis()))
                        binding.swipeToRefreshLayoutTweets.isRefreshing = false

                    }

                    if (tweetList.isEmpty()) {

                        binding.rcView.visibility = View.GONE
                        binding.tweetsPageAnView.visibility = View.VISIBLE
                        binding.lottieInfoTv.visibility = View.VISIBLE

                    } else {

                        binding.rcView.visibility = View.VISIBLE
                        binding.tweetsPageAnView.visibility = View.GONE
                        binding.lottieInfoTv.visibility = View.GONE
                        userPostAdapter.setData(tweetList)
                        userPostAdapter.notifyDataSetChanged()

                    }
                }

            } else {
                binding.tweetsRootLayout.snackbarError(
                    requireActivity().findViewById(R.id.tweets_root_layout),
                    error = response.message(),
                    ""){}
            }
        }
    }
}