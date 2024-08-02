package com.example.twitturin.core.extensions

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

inline fun <reified T : FragmentStateAdapter> ViewPager2.setupWithAdapter(
    tb: TabLayout,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    adapterProvider: (FragmentManager, Lifecycle) -> T,
    crossinline onConfigureTab: (tab: TabLayout.Tab, position: Int) -> Unit
) {
    this.adapter = adapterProvider(fragmentManager, lifecycle)
    TabLayoutMediator(tb, this) { tab, position -> onConfigureTab(tab, position) }.attach()
}