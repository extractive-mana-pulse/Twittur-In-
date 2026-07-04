package com.example.twitturin.auth.presentation.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.login.screens.SignInScreen
import com.example.twitturin.auth.presentation.login.vm.SignInViewModel
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val signInViewModel by viewModels<SignInViewModel>()
    private val stayInViewModel by viewModels<StayInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SignInScreen(
                    viewModel = signInViewModel,
                    onSignedIn = {
                        SessionManager(requireContext()).saveToken(signInViewModel.token.value.toString())
                        SessionManager(requireContext()).saveUserID(signInViewModel.userId.value.toString())
                        findNavController().navigate(R.id.action_signInFragment_to_stayInFragment)
                    },
                    onSignUp = { findNavController().navigate(R.id.action_signInFragment_to_kindFragment) }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (stayInViewModel.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }
}
