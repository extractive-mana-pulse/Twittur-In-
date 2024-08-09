package com.example.twitturin.home.presentation.settings.bottom_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.appBNVDialog
import com.example.twitturin.core.extensions.expandedSheet
import com.example.twitturin.core.extensions.setupWithAdapter
import com.example.twitturin.databinding.FragmentCustomBNVBinding
import com.example.twitturin.home.presentation.settings.bottom_bar.adapter.BottomNavViewAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomBNVFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentCustomBNVBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        binding.apply {
            customizeBtn.setOnClickListener { requireActivity().appBNVDialog(bottomNavView) }
            customBnvToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            vp2.setupWithAdapter(tb, childFragmentManager, lifecycle, { fm, lc -> BottomNavViewAdapter(fm, lc) },
                { _, pos ->
                    when (pos) {
                        0 -> {}
                        1 -> {}
                        2 -> {}
                    }
                }
            )
        }
        expandedSheet()
    }
}