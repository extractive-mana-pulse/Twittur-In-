package com.example.twitturin.ui.fragments.bottom_sheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentEditTweetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class EditTweetFragment : Fragment() {

    private lateinit var binding : FragmentEditTweetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val baseInflater = LayoutInflater.from(requireActivity())
        binding = FragmentEditTweetBinding.inflate(baseInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}