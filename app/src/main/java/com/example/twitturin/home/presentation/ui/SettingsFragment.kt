package com.example.twitturin.home.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private val binding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        /**
         * TODO. Build options to user change UI elements.
         * UI elements such as ->
         * Bottom navigation view with 3 options. e.g. labeled, unlabeled, without label at all.
         * FAB 3 options. change corners. Make Circled. Rounded, or as chat icon.
         * Theme. 2 options. Material3 or custom private colors.
         * Add more......
         * */

    }
}