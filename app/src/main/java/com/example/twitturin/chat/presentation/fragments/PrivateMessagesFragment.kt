package com.example.twitturin.chat.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.twitturin.R
import com.example.twitturin.chat.presentation.adapters.PrivateMessagesViewPagerAdapter
import com.example.twitturin.core.extensions.setupWithAdapter
import com.example.twitturin.databinding.FragmentPrivateMessagesBinding

class PrivateMessagesFragment : Fragment() {

    private val binding by lazy { FragmentPrivateMessagesBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.privateMessagesFragment = this
        binding.apply {
            privateMessagesVp2.setupWithAdapter(privateMessagesTabLayout, childFragmentManager, lifecycle, { fm, lc -> PrivateMessagesViewPagerAdapter(fm, lc) },
                { _, pos ->
                    when (pos) {
                        0 -> {}
                        1 -> {}
                        2 -> {}
                    }
                }
            )
            privateMessagesTabLayout.getTabAt(0)?.setIcon(R.drawable.chat)
            privateMessagesTabLayout.getTabAt(1)?.setIcon(R.drawable.groups)
        }
    }
}