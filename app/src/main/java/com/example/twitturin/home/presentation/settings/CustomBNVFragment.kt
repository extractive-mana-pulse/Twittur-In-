package com.example.twitturin.home.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.appBNVDialog
import com.example.twitturin.databinding.FragmentCustomBNVBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

class CustomBNVFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentCustomBNVBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        binding.customizeBtn.setOnClickListener { requireActivity().appBNVDialog(bottomNavView) }

        binding.customBnvToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.vp2.adapter = BottomNavViewAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tb, binding.vp2) { _, pos ->
            when (pos) {
                0 -> {}
                1 -> {}
                2 -> {}
            }
        }.attach()


        val bottomSheet : FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!

        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.apply {
            isDraggable = false
            peekHeight = resources.displayMetrics.heightPixels
            state = BottomSheetBehavior.STATE_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }
}