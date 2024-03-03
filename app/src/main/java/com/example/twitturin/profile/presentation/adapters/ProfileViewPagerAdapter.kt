package com.example.twitturin.profile.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.twitturin.ui.fragments.view_pager.LikesFragment
import com.example.twitturin.tweet.presentation.fragments.TweetsFragment

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