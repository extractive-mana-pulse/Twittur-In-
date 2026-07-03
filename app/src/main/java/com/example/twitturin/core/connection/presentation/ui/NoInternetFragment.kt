package com.example.twitturin.core.connection.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.connection.presentation.screens.NoInternetScreen
import com.example.twitturin.core.extensions.checkConnection

class NoInternetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NoInternetScreen(
                    onTryAgain = { requireContext().checkConnection(findNavController()) }
                )
            }
        }
    }
}
