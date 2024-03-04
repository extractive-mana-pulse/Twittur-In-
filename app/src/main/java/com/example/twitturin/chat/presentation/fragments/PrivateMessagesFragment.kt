package com.example.twitturin.chat.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.example.twitturin.chat.presentation.adapters.PrivateMessagesViewPagerAdapter
import com.example.twitturin.databinding.FragmentPrivateMessagesBinding
import com.example.twitturin.profile.presentation.adapters.ProfileViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class PrivateMessagesFragment : Fragment() {

    private val binding by lazy { FragmentPrivateMessagesBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.privateMessagesFragment = this

        val adapter = PrivateMessagesViewPagerAdapter(childFragmentManager, lifecycle)
        binding.privateMessagesVp2.adapter = adapter
        TabLayoutMediator(binding.privateMessagesTabLayout, binding.privateMessagesVp2) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getString(R.string.chat)
                }
                1 -> {
                    tab.text = resources.getString(R.string.groups)
                }
            }
        }.attach()
    }
}