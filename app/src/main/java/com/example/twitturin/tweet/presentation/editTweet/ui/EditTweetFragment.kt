package com.example.twitturin.tweet.presentation.editTweet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.tweet.presentation.editTweet.screens.EditTweetScreen
import com.example.twitturin.tweet.presentation.editTweet.vm.EditTweetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTweetFragment : Fragment() {

    private val editTweetViewModel: EditTweetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val content = arguments?.getString("description").orEmpty()
        val tweetId = arguments?.getString("tweetId").orEmpty()
        val token = "Bearer ${SessionManager(requireContext()).getToken()}"
        return ComposeView(requireContext()).apply {
            setContent {
                EditTweetScreen(
                    viewModel = editTweetViewModel,
                    initialContent = content,
                    tweetId = tweetId,
                    token = token,
                    onClose = { findNavController().navigateUp() }
                )
            }
        }
    }
}
