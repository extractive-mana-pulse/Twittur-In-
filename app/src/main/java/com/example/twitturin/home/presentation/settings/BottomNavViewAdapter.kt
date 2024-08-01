package com.example.twitturin.home.presentation.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.twitturin.home.presentation.settings.onBoarding.LabeledFragment
import com.example.twitturin.home.presentation.settings.onBoarding.SelectedFragment
import com.example.twitturin.home.presentation.settings.onBoarding.UnlabeledFragment

class BottomNavViewAdapter(frag: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(frag, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> { LabeledFragment() }
            1 -> { UnlabeledFragment() }
            2 -> { SelectedFragment() }
            else -> { Fragment() }
        }
    }
}