package com.example.twitturin.home.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.snackbar
import com.example.twitturin.databinding.FragmentSettingsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.CornerFamily

class SettingsFragment : Fragment() {

    private val binding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            settingsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            inactiveLayout.setOnClickListener { snackbarView.snackbar(snackbarView, resources.getString(R.string.developing)) }
            themeCustomizationLayout.setOnClickListener { snackbarView.snackbar(snackbarView, resources.getString(R.string.developing)) }
            fabCustomizationLayout.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_customizeFABFragment) }
            bottomNavigationViewCustomizationLayout.setOnClickListener { findNavController().navigate(R.id.action_settingsFragment_to_customBNVFragment) }

        }

        /**
         * TODO. Build options to user change UI elements.
         * UI elements such as ->
         * Bottom navigation view with 3 options. e.g. labeled, unlabeled, without label at all.
         * TODO: FAB 3 options. change corners. Make Circled. Rounded, or as chat icon. DO IT LATER.
         * FAB 2 options. Extended, Default
         * Theme. 2 options. Material3 or custom private colors.
         * Option to auto-delete user's account & auto-sign out. after 3,6,12 months.
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

    private fun changeFabShape() {
        val fabSize = resources.getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_normal)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.add_post)
        fab.shapeAppearanceModel = fab.shapeAppearanceModel.toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, fabSize / 2f) // Set the corner radius to half the FAB size
            .build()
    }
}