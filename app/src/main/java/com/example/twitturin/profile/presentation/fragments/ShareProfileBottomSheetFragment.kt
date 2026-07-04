package com.example.twitturin.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.example.twitturin.core.extensions.copyToClipboard
import com.example.twitturin.core.extensions.expandedSheet
import com.example.twitturin.core.extensions.shareUrl
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.profile.presentation.screens.ShareProfileBottomSheetScreen
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = SessionManager(requireContext()).getUserId().orEmpty()
        val url = "https://twitturin.onrender.com/users/$userId"
        return ComposeView(requireContext()).apply {
            setContent {
                ShareProfileBottomSheetScreen(
                    viewModel = profileViewModel,
                    userId = userId,
                    onBack = { dismiss() },
                    onShare = { requireContext().shareUrl(url) },
                    onCopy = { requireContext().copyToClipboard(url) }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandedSheet()
    }
}
