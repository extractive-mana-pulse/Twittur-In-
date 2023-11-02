package com.example.twitturin.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.twitturin.presentation.fragments.viewPagerFragments.LikesFragment
import com.example.twitturin.presentation.fragments.viewPagerFragments.TweetsFragment

class ViewPagedAdapter(frag: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(frag, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                // this fragments create only for profile page. inside profile fragment we've viewPager2 which works with this fragments
                TweetsFragment()
            }
            1 -> {
                // this fragments create only for profile page. inside profile fragment we've viewPager2 which works with this fragments
                LikesFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}