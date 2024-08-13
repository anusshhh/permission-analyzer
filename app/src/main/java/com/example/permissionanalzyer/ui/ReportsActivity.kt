package com.example.permissionanalzyer.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.permissionanalzyer.adapter.AppInfoAdapter
import com.example.permissionanalzyer.databinding.ActivityReportsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class ReportsActivity : AppCompatActivity() {
    lateinit var binding: ActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the app name and package name from the intent
        val appName = intent.getStringExtra("APP_NAME")
        val selectedPackageName = intent.getStringExtra("PACKAGE_NAME") ?: ""

        try {
            // Get the permissions for the package
            val packageInfo =
                packageManager.getPackageInfo(selectedPackageName, PackageManager.GET_PERMISSIONS)
            val requestedPermissions = packageInfo.requestedPermissions

            if (requestedPermissions != null) {
                val adapter = AppInfoAdapter()
                adapter.submitList(requestedPermissions.toList())
                binding.permissionList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                binding.permissionList.adapter = adapter

            } else {
                Log.d("PermissionActivity", "No permissions found for package: $selectedPackageName")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("PermissionActivity", "Package not found: $selectedPackageName", e)
        }

        if (appName != null) {
            fetchTrackersForApp(selectedPackageName)
        }
        
    }

    private fun fetchTrackersForApp(selectedPackageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://reports.exodus-privacy.eu.org/en/reports/$selectedPackageName/latest/" // Hypothetical URL
                val document = Jsoup.connect(url).get()

                // Extract tracker information from the document
                val trackerList = ArrayList<String>()

                // Extract the trackers
                val trackerElements: Elements = document.select("p a.link.black")
                val badgeElements: Elements =
                    document.select("span.badge.badge-pill.badge-outline-primary")
                badgeElements.add(0, null)

                for (i in trackerElements.indices) {
                    val trackerElement = trackerElements[i]
                    val badgeElement = if ((i < badgeElements.size)) badgeElements[i] else null

                    val trackerName = trackerElement.text()
                    val trackerType = if ((badgeElement != null)) badgeElement.text() else " N/A "

                    // Build a string representing the tracker data
                    val data = "Tracker Name: $trackerName\nTracker Type: $trackerType"
                    trackerList.add(data)

                    // Log the tracker data to the console
                    Log.d("TrackerData", data)
                }
                withContext(Dispatchers.Main) {
                    // Process and display the tracker information
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Error: ${e.message}")
                }
            }
        }
    }

}

