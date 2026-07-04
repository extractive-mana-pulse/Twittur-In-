package com.example.twitturin.profile.presentation.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import com.example.twitturin.profile.presentation.screens.FullScreenImageScreen

@Suppress("DEPRECATION")
class FullScreenImageFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bitmap = arguments?.getParcelable<Bitmap>("image")
        return ComposeView(requireContext()).apply {
            setContent {
                FullScreenImageScreen(bitmap = bitmap?.asImageBitmap())
            }
        }
    }
}
