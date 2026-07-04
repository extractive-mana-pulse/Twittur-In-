package com.example.twitturin.tweet.presentation.postTweet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.tweet.presentation.postTweet.screens.PublicPostScreen
import com.example.twitturin.tweet.presentation.postTweet.vm.PostTweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublicPostFragment : Fragment() {

    private val postTweetViewModel by viewModels<PostTweetViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val token = "Bearer ${SessionManager(requireContext()).getToken()}"
        return ComposeView(requireContext()).apply {
            setContent {
                PublicPostScreen(
                    viewModel = postTweetViewModel,
                    onBack = { findNavController().navigateUp() },
                    onPost = { content -> postTweetViewModel.postTheTweet(content, token) },
                    onPosted = { findNavController().navigate(R.id.action_publicPostFragment_to_homeFragment) }
                )
            }
        }
    }
}
