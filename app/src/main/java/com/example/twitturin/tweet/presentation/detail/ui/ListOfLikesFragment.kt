package com.example.twitturin.tweet.presentation.detail.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.twitturin.R
import com.example.twitturin.databinding.FragmentListOfLikesBinding

class ListOfLikesFragment : Fragment() {

    private val binding by lazy { FragmentListOfLikesBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            listOfLikesToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
//            listOfLikesRcView.adapter = adapter
        }
    }
}