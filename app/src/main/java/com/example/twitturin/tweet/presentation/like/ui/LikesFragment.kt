package com.example.twitturin.tweet.presentation.like.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.extensions.vertical
import com.example.twitturin.databinding.FragmentLikesBinding
import com.example.twitturin.home.presentation.adapter.HomeAdapter
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.core.extensions.snackbarError
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesFragment : Fragment() {

    private val tweetViewModel : TweetViewModel by viewModels()
    private val binding by lazy { FragmentLikesBinding.inflate(layoutInflater) }
    private val homeAdapter by lazy { HomeAdapter(homeClickEvents = ::homeClickEvent) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        binding.apply {
            val userId = SessionManager(requireContext()).getUserId()

            rcView.vertical().adapter = homeAdapter

            tweetViewModel.getLikedPosts(userId!!)

            tweetViewModel.likedPosts.observe(requireActivity()) { response ->

                if (response.isSuccessful) {

                    response.body()?.let { tweets ->

                        val tweetList: MutableList<Tweet> = tweets.toMutableList()
                        homeAdapter.differ.submitList(tweetList)

                        if (tweetList.isEmpty()) {
                            rcView.visibility = View.GONE
                            likesPageAnView.visibility = View.VISIBLE
                            lottieInfoTv.visibility = View.VISIBLE
                        } else {
                            rcView.visibility = View.VISIBLE
                            likesPageAnView.visibility = View.GONE
                            lottieInfoTv.visibility = View.GONE
                            tweetViewModel.getLikedPosts(userId)
                        }
                    }
                } else {
                    likesRootLayout.snackbarError(
                        likesRootLayout,
                        error = response.body().toString(),
                        ""){}
                }
            }
        }
    }

    private fun homeClickEvent(homeClickEvents: HomeAdapter.HomeClickEvents, tweet: Tweet) {

        when(homeClickEvents) {

            HomeAdapter.HomeClickEvents.ITEM -> {
                val bundle = Bundle().apply {
                    putString("userAvatar", tweet.author?.profilePicture)
                    putString("fullname", tweet.author?.fullName)
                    putString("username", tweet.author?.username)
                    putString("post_description", tweet.content)
                    putString("likes", tweet.likes.toString())
                    putString("createdAt", tweet.createdAt)
                    putString("updatedAt", tweet.updatedAt)
                    putString("userId", tweet.author?.id)
                    putString("id", tweet.id)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }

            HomeAdapter.HomeClickEvents.HEART -> {
                Toast.makeText(requireContext(), "In Progress", Toast.LENGTH_SHORT).show()/*Snackbar.make(binding.homeRootLayout, R.string.in_progress, Snackbar.LENGTH_SHORT).show()*/ }

            HomeAdapter.HomeClickEvents.SHARE -> { requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}") }

            HomeAdapter.HomeClickEvents.REPLY -> {
                val bundle = Bundle().apply {
                    putString("userAvatar", tweet.author?.profilePicture)
                    putString("fullname", tweet.author?.fullName)
                    putString("username", tweet.author?.username)
                    putString("post_description", tweet.content)
                    putString("likes", tweet.likes.toString())
                    putString("updatedAt", tweet.updatedAt)
                    putString("createdAt", tweet.createdAt)
                    putString("userId", tweet.author?.id)
                    putBoolean("activateEditText", true)
                    putString("id", tweet.id)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }
        }
    }
}