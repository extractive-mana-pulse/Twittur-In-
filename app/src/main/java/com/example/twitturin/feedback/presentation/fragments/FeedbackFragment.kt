package com.example.twitturin.feedback.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.extensions.sendEmail
import com.example.twitturin.feedback.presentation.screens.FeedbackScreen

class FeedbackFragment : Fragment() {

    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            FeedbackScreen(
                onBack = { findNavController().navigateUp() },
                onSend = { subject, message -> sendEmail(subject, message) }
            )
        }
    }
}
