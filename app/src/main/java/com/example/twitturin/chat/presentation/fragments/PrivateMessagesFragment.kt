package com.example.twitturin.chat.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.example.twitturin.chat.presentation.adapters.PrivateMessagesViewPagerAdapter
import com.example.twitturin.databinding.FragmentPrivateMessagesBinding
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

                }
                1 -> {

                }
            }
        }.attach()

        binding.privateMessagesTabLayout.getTabAt(0)?.setIcon(R.drawable.chat)
        binding.privateMessagesTabLayout.getTabAt(1)?.setIcon(R.drawable.groups)
    }
}