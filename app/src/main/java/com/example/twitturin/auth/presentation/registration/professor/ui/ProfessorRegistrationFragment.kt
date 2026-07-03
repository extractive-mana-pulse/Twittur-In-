package com.example.twitturin.auth.presentation.registration.professor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.registration.professor.screens.ProfessorRegistrationScreen
import com.example.twitturin.auth.presentation.registration.vm.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfessorRegistrationFragment : Fragment() {

    private val signUpViewModel by viewModels<SignUpViewModel>()
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
            ProfessorRegistrationScreen(
                viewModel = signUpViewModel,
                onBack = { findNavController().navigateUp() },
                onRegistered = { findNavController().navigate(R.id.action_professorRegistrationFragment_to_signInFragment) }
            )
        }
    }
}
