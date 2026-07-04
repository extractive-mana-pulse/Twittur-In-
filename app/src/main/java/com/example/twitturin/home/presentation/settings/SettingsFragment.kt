package com.example.twitturin.home.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.BuildConfig
import com.example.twitturin.R
import com.example.twitturin.home.presentation.settings.screens.SettingsScreen

class SettingsFragment : Fragment() {

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
            SettingsScreen(
                appVersion = BuildConfig.VERSION_NAME,
                onBack = { findNavController().navigateUp() },
                onCustomizeBottomBar = { findNavController().navigate(R.id.action_settingsFragment_to_customBNVFragment) },
                onCustomizeFab = { findNavController().navigate(R.id.action_settingsFragment_to_customizeFABFragment) }
            )
        }
    }
}
