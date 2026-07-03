package com.example.twitturin.home.presentation.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.example.twitturin.home.presentation.screens.LanguageBottomSheetScreen
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class LanguageBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LanguageBottomSheetScreen(
                    onSave = { lang ->
                        setLocale(lang)
                        requireActivity().recreate()
                        dismiss()
                    }
                )
            }
        }
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(
            config,
            requireActivity().baseContext.resources.displayMetrics
        )
        val editor = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("lang", lang)
        editor.apply()
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}
