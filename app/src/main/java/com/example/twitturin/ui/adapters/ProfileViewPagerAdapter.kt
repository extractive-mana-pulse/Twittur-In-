package com.example.twitturin.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.twitturin.ui.fragments.viewPagerFragments.LikesFragment
import com.example.twitturin.ui.fragments.viewPagerFragments.TweetsFragment

class ProfileViewPagerAdapter(frag: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(frag, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                TweetsFragment()
            }
            1 -> {
                LikesFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}