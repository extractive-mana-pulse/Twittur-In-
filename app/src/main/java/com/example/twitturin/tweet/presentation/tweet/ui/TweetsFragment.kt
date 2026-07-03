package com.example.twitturin.tweet.presentation.tweet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.defaultDialog
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.detail.presentation.sealed.TweetDelete
import com.example.twitturin.tweet.domain.model.Tweet
import com.example.twitturin.tweet.presentation.tweet.screens.TweetsScreen
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TweetsFragment : Fragment() {

    private val tweetViewModel: TweetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = SessionManager(requireContext()).getUserId().orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                TweetsScreen(
                    viewModel = tweetViewModel,
                    userId = userId,
                    onBack = { findNavController().navigateUp() },
                    onTweetClick = { tweet ->
                        findNavController().navigate(
                            R.id.detailFragment,
                            Bundle().apply { putParcelable("tweet", tweet) }
                        )
                    },
                    onReplyClick = { tweet ->
                        findNavController().navigate(
                            R.id.detailFragment,
                            Bundle().apply {
                                putParcelable("tweet", tweet)
                                putBoolean("activateEditText", true)
                            }
                        )
                    },
                    onShare = { tweet ->
                        requireContext().shareUrl("https://twitturin.onrender.com/tweets/${tweet.id}")
                    },
                    onEdit = { tweet ->
                        findNavController().navigate(
                            R.id.editTweetFragment,
                            Bundle().apply {
                                putString("description", tweet.content)
                                putString("tweetId", tweet.id)
                            }
                        )
                    },
                    onDelete = { tweet -> confirmDelete(tweet, userId) }
                )
            }
        }
    }

    private fun confirmDelete(tweet: Tweet, userId: String) {
        defaultDialog(
            getString(R.string.delete_tweet_title),
            getString(R.string.delete_tweet_message),
            actionYesClicked = {
                tweetViewModel.deleteTweet(
                    tweet.id!!,
                    "Bearer ${SessionManager(requireContext()).getToken()}"
                )
                tweetViewModel.deleteTweetResult.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is TweetDelete.Success -> tweetViewModel.getUserTweet(userId)
                        is TweetDelete.Error -> {}
                    }
                }
            },
            actionNoClicked = {}
        )
    }
}
