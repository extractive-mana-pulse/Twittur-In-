package com.example.twitturin.detail.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.detail.presentation.screens.MoreSettingsDetailScreen
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.follow.presentation.followers.sealed.Follow
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreSettingsDetailFragment : BottomSheetDialogFragment() {

    private val tweetViewModel by viewModels<TweetViewModel>()
    private val followViewModel by viewModels<FollowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tweet = MoreSettingsDetailFragmentArgs.fromBundle(requireArguments()).tweet
        val token = "Bearer ${SessionManager(requireContext()).getToken()}"
        val isOwn = tweet.author?.id == SessionManager(requireContext()).getUserId()
        return ComposeView(requireContext()).apply {
            setContent {
                MoreSettingsDetailScreen(
                    username = tweet.author?.username.orEmpty(),
                    isOwnTweet = isOwn,
                    onFollow = {
                        followViewModel.followUser(tweet.author?.id!!, token)
                        followViewModel.follow.observe(viewLifecycleOwner) { result ->
                            if (result is Follow.Success) dismiss()
                        }
                    },
                    onEdit = {
                        findNavController().navigate(
                            R.id.editTweetFragment,
                            Bundle().apply {
                                putString("description", tweet.content)
                                putString("tweetId", tweet.id)
                            }
                        )
                        dismiss()
                    },
                    onDelete = { confirmDelete(tweet, token) },
                    onReport = {
                        findNavController().navigate(R.id.reportFragment)
                        dismiss()
                    }
                )
            }
        }
    }

    private fun confirmDelete(tweet: Tweet, token: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.delete_tweet_title))
            .setMessage(getString(R.string.delete_tweet_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                tweetViewModel.deleteTweet(tweet.id!!, token)
                tweetViewModel.deleteTweetResult.observe(viewLifecycleOwner) { result ->
                    if (result is TweetDelete.Success) {
                        dismiss()
                        findNavController().navigate(R.id.homeFragment)
                    }
                }
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> dismiss() }
            .show()
    }
}
