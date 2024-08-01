package com.example.twitturin.home.presentation.settings.bottom_bar.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.twitturin.databinding.FragmentLabeledBinding


class LabeledFragment : Fragment() {

    private val binding by lazy { FragmentLabeledBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

}