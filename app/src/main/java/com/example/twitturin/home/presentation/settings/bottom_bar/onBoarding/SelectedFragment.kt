package com.example.twitturin.home.presentation.settings.bottom_bar.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.example.twitturin.home.presentation.settings.bottom_bar.onBoarding.screens.BnvPreviewScreen

class SelectedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BnvPreviewScreen(
                    imageRes = R.drawable.selected,
                    description = "This view represents a bottom navigation view in SELECTED state."
                )
            }
        }
    }
}
