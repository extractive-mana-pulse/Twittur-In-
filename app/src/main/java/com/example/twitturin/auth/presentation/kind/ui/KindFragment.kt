package com.example.twitturin.auth.presentation.kind.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.kind.screens.KindScreen

class KindFragment : Fragment() {

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
        composeView.setContent {
            KindScreen(
                onBack = { findNavController().navigateUp() },
                onProfessor = { findNavController().navigate(R.id.action_kindFragment_to_professorRegistrationFragment) },
                onStudent = { findNavController().navigate(R.id.action_kindFragment_to_studentRegistrationFragment) }
            )
        }
    }
}
