package com.example.permissionanalzyer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.permissionanalzyer.R
import com.example.permissionanalzyer.adapter.ViewPagerAdapter
import com.example.permissionanalzyer.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Installed Apps"
                1 -> "System Apps"
                else -> null
            }
        }.attach()
    }
}