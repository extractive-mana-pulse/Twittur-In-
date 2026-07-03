package com.example.twitturin.home.presentation.settings.fab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.extensions.expandedSheet
import com.example.twitturin.home.presentation.settings.fab.screens.CustomizeFabScreen
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomizeFABFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CustomizeFabScreen(
                    onBack = { findNavController().navigateUp() },
                    onCustomize = {}
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandedSheet()
    }
}
