package com.example.twitturin.home.presentation.settings.fab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.core.extensions.expandedSheet
import com.example.twitturin.databinding.FragmentCustomizeFABBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomizeFABFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentCustomizeFABBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.add_post)

        binding.apply {
            customFabToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            customizeBtn.setOnClickListener {  }
        }
        expandedSheet()
    }
}