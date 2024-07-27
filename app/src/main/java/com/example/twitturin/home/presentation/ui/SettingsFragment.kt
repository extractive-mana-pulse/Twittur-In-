package com.example.twitturin.home.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private val binding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            settingsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            testBtn.setOnClickListener {
                changeAppTheme()
            }
        }

        /**
         * TODO. Build options to user change UI elements.
         * UI elements such as ->
         * Bottom navigation view with 3 options. e.g. labeled, unlabeled, without label at all.
         * FAB 3 options. change corners. Make Circled. Rounded, or as chat icon.
         * Theme. 2 options. Material3 or custom private colors.
         * Add more......
         * */

    }

    private fun changeAppTheme() {
        // Get the current theme
        val currentTheme = AppCompatDelegate.getDefaultNightMode()

        // Set the new theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        requireActivity().setTheme(R.style.Custom_LightTheme)

        // Save the state of the changed theme
        val sharedPreferences = requireActivity().getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("theme_mode", currentTheme)
        editor.apply()
    }
}