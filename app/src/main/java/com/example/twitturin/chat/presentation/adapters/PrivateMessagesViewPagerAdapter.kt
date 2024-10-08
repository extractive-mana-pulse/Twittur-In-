package com.example.twitturin.chat.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.twitturin.chat.presentation.fragments.ChatsFragment
import com.example.twitturin.chat.presentation.fragments.GroupsFragment

class PrivateMessagesViewPagerAdapter(frag: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(frag, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ChatsFragment()
            }

            1 -> {
                GroupsFragment()
            }

            else -> {
                Fragment()
            }
        }
    }
}