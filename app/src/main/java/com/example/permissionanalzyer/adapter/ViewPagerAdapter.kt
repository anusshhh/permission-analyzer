package com.example.permissionanalzyer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.permissionanalzyer.ui.AppsFragment
import com.example.permissionanalzyer.ui.SystemAppsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AppsFragment()
            1 -> SystemAppsFragment()
            else -> AppsFragment()
        }
    }
}
