package com.example.twitturin.tweet.presentation.like.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.tweet.presentation.like.screens.LikesScreen
import com.example.twitturin.tweet.presentation.tweet.vm.TweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesFragment : Fragment() {

    private val tweetViewModel by viewModels<TweetViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = SessionManager(requireContext()).getUserId().orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                LikesScreen(
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
                    }
                )
            }
        }
    }
}
