package com.example.twitturin.tweet.presentation.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.sendEmail
import com.example.twitturin.tweet.presentation.report.screens.ReportScreen

class ReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ReportScreen(
                    onBack = { findNavController().navigateUp() },
                    onSubmit = { subject, context ->
                        sendEmail(subject, context)
                        findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
                    }
                )
            }
        }
    }
}
