package com.example.twitturin.follow.presentation.followers.ui

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
import com.example.twitturin.follow.presentation.followers.screens.FollowersScreen
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowersFragment : Fragment() {

    private val followViewModel: FollowViewModel by viewModels()
    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also { composeView = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        composeView.setContent {
            FollowersScreen(
                viewModel = followViewModel,
                userId = session.getUserId().orEmpty(),
                token = "Bearer ${session.getToken()}",
                onBack = { findNavController().navigateUp() },
                onUserClick = { findNavController().navigate(R.id.observeProfileFragment) }
            )
        }
    }
}
