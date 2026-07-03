package com.example.twitturin.auth.presentation.stayIn.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.auth.presentation.stayIn.screens.StayInScreen
import com.example.twitturin.auth.presentation.stayIn.vm.StayInViewModel
import com.example.twitturin.core.manager.SessionManager
import com.example.twitturin.profile.presentation.vm.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StayInFragment : Fragment() {

    private val stayInViewModel: StayInViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId = SessionManager(requireContext()).getUserId().orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                StayInScreen(
                    viewModel = profileViewModel,
                    userId = userId,
                    onSave = {
                        stayInViewModel.setUserLoggedIn(true)
                        findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
                    },
                    onNotSave = {
                        stayInViewModel.setUserLoggedIn(false)
                        findNavController().navigate(R.id.action_stayInFragment_to_homeFragment)
                    }
                )
            }
        }
    }
}
