package com.example.twitturin.auth.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentKindBinding

class KindFragment : Fragment() {

    private lateinit var binding : FragmentKindBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentKindBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            professorKindBtn.setOnClickListener {
                findNavController().navigate(R.id.action_kindFragment_to_professorRegistrationFragment)
            }

            studentKindBtn.setOnClickListener {
                findNavController().navigate(R.id.action_kindFragment_to_studentRegistrationFragment)
            }

            backBtnKind.setOnClickListener {
                findNavController().navigate(R.id.action_kindFragment_to_signInFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        window.apply {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            val lightMode = AppCompatDelegate.MODE_NIGHT_NO
            statusBarColor = if (currentNightMode == lightMode) {
                ContextCompat.getColor(requireActivity(), R.color.md_theme_light_surface)
            } else {
                ContextCompat.getColor(requireActivity(), R.color.md_theme_dark_surface)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val window = requireActivity().window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), com.google.android.material.R.color.m3_sys_color_light_surface_container)
    }
}