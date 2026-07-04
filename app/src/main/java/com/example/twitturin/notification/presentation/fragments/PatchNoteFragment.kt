package com.example.twitturin.notification.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.notification.presentation.screens.PatchNoteScreen
import com.example.twitturin.notification.presentation.vm.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatchNoteFragment : Fragment() {

    private val notificationViewModel by viewModels<NotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PatchNoteScreen(
                    viewModel = notificationViewModel,
                    onBack = { findNavController().navigateUp() },
                    onUpdate = { url -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                )
            }
        }
    }
}
