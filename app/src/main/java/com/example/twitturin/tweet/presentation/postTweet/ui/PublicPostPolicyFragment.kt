package com.example.twitturin.tweet.presentation.postTweet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.tweet.presentation.postTweet.screens.PublicPostPolicyScreen

class PublicPostPolicyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PublicPostPolicyScreen(
                    onBack = { findNavController().navigateUp() },
                    onAgree = { findNavController().navigate(R.id.action_publicPostPolicyFragment_to_publicPostFragment) }
                )
            }
        }
    }
}
