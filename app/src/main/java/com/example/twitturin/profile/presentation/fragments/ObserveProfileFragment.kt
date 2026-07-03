package com.example.twitturin.profile.presentation.fragments

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
import com.example.twitturin.follow.presentation.vm.FollowViewModel
import com.example.twitturin.profile.presentation.screens.ObserveProfileScreen
import com.example.twitturin.search.domain.model.SearchUser
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ObserveProfileFragment : Fragment() {

    private val followViewModel by viewModels<FollowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val user = (arguments?.getParcelable("item") as? SearchUser) ?: SearchUser()
        val token = "Bearer ${SessionManager(requireContext()).getToken()}"
        return ComposeView(requireContext()).apply {
            setContent {
                ObserveProfileScreen(
                    user = user,
                    viewModel = followViewModel,
                    token = token,
                    onBack = { findNavController().navigateUp() },
                    onShare = { findNavController().navigate(R.id.shareProfileBottomSheetFragment) }
                )
            }
        }
    }
}
