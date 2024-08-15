package com.example.permissionanalzyer.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.permissionanalzyer.adapter.AppListAdapter
import com.example.permissionanalzyer.databinding.FragmentSystemAppsBinding
import com.example.permissionanalzyer.utils.getAppName
import com.example.permissionanalzyer.utils.getInstalledApplications

class SystemAppsFragment : Fragment() {
    lateinit var binding: FragmentSystemAppsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSystemAppsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val systemAppList = requireContext().getInstalledApplications().filter {
            (it.flags and ApplicationInfo.FLAG_SYSTEM != 0)
        }

        val adapter = AppListAdapter { appInfo ->
            val intent = Intent(requireContext(), ReportsActivity::class.java)
            val appName = appInfo.getAppName(requireContext())
            val packageName = appInfo.packageName
            intent.putExtra("APP_NAME", appName)
            intent.putExtra("PACKAGE_NAME", packageName)
            startActivity(intent)
        }.apply {
            submitList(systemAppList)
        }
        binding.rvSystemApps.adapter = adapter
        binding.rvSystemApps.layoutManager = GridLayoutManager(requireContext(),3)

    }
}


