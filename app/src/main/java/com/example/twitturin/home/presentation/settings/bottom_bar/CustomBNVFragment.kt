package com.example.twitturin.home.presentation.settings.bottom_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.appBNVDialog
import com.example.twitturin.core.extensions.expandedSheet
import com.example.twitturin.home.presentation.settings.bottom_bar.screens.CustomBnvScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomBNVFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CustomBnvScreen(
                    onBack = { findNavController().navigateUp() },
                    onCustomize = {
                        val bottomNavView =
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
                        requireActivity().appBNVDialog(bottomNavView)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandedSheet()
    }
}
