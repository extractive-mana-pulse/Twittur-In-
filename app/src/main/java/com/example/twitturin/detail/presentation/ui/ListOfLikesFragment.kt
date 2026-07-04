package com.example.twitturin.detail.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.detail.presentation.screens.ListOfLikesScreen
import com.example.twitturin.detail.presentation.vm.ListOfLikesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListOfLikesFragment : Fragment() {

    private val listOfLikesViewModel: ListOfLikesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tweetId = arguments?.getString("id").orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                ListOfLikesScreen(
                    viewModel = listOfLikesViewModel,
                    tweetId = tweetId,
                    onBack = { findNavController().navigateUp() }
                )
            }
        }
    }
}
