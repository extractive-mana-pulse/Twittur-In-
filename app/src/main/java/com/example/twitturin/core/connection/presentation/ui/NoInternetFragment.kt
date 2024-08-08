package com.example.twitturin.core.connection.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.twitturin.core.extensions.checkConnection
import com.example.twitturin.databinding.FragmentNoInternetBinding

class NoInternetFragment : Fragment() {

    private val binding by lazy { FragmentNoInternetBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tryAgainBtn.setOnClickListener { requireContext().checkConnection(findNavController()) }
    }
 }