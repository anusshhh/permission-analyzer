package com.example.permissionanalzyer.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.permissionanalzyer.R
import com.example.permissionanalzyer.adapter.AppInfoAdapter
import com.example.permissionanalzyer.databinding.ActivityReportsBinding
import com.example.permissionanalzyer.utils.countDangerousPermissions
import com.example.permissionanalzyer.utils.getAppIcon
import com.example.permissionanalzyer.utils.getAppName
import com.example.permissionanalzyer.utils.gone
import com.example.permissionanalzyer.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class ReportsActivity : AppCompatActivity() {
    lateinit var binding: ActivityReportsBinding
    lateinit var permissionList: List<String>
    lateinit var trackerList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressbar.visible()

        // Retrieve the app name and package name from the intent
        val appName = intent.getStringExtra("APP_NAME") ?: ""
        val selectedPackageName = intent.getStringExtra("PACKAGE_NAME") ?: ""

        val appInfo = packageManager.getApplicationInfo(selectedPackageName, 0)

        updateAppInfoUI(appInfo)

        permissionList = fetchPermissionList(selectedPackageName)
        if (permissionList.isNotEmpty()) {
            val adapter = AppInfoAdapter(true)
            adapter.submitList(permissionList)
            binding.rvPermissionList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvPermissionList.adapter = adapter
        } else {
            binding.rvPermissionList.gone()
            binding.groupPermissionNoData.visible()

            Log.d(
                "PermissionActivity",
                "No permissions found for package: $selectedPackageName"
            )
        }
        binding.tvPermissionCount.text = permissionList.size.toString()

        if (appName.isNotBlank()) {
            lifecycleScope.launch(Dispatchers.Main) {
                trackerList = fetchTrackersForApp(selectedPackageName)
                val adapter = AppInfoAdapter()
                if (trackerList.isNotEmpty()) {
                    adapter.submitList(trackerList)
                    binding.rvTrackerList.layoutManager =
                        LinearLayoutManager(
                            this@ReportsActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    binding.rvTrackerList.adapter = adapter
                } else {
                    binding.rvTrackerList.gone()
                    binding.groupTrackerNoData.visible()
                }
                binding.tvTrackerCount.text = trackerList.size.toString()
                binding.progressbar.gone()

                gaugeAnimation()
            }
        }

        registerClickListeners()

    }

    private fun gaugeAnimation() {
        val gaugeHelper = GaugeHelper(
            onRotateDegreeChanged = { degree -> binding.gaugeView.setRotateDegree(degree) },
            onSweepAngleFirstChartChanged = { angle ->
                binding.gaugeView.setSweepAngleFirstChart(angle)
                binding.tvPrivacyRiskResult.apply{
                    text = getString(R.string.low)
                    setTextColor(ContextCompat.getColor(context, R.color.low_green))
                }

            },
            onSweepAngleSecondChartChanged = { angle ->
                binding.gaugeView.setSweepAngleSecondChart(angle)
                binding.tvPrivacyRiskResult.apply{
                    text = getString(R.string.moderate)
                    setTextColor(ContextCompat.getColor(context, R.color.mid_yellow))
                }
            },
            onSweepAngleThirdChartChanged = { angle ->
                binding.gaugeView.setSweepAngleThirdChart(angle)
                binding.tvPrivacyRiskResult.apply{
                    text = getString(R.string.high)
                    setTextColor(ContextCompat.getColor(context, R.color.high_red))
                }
            }
        )
        val appScore = getPrivacyRiskScore()
        gaugeHelper.startRunning(appScore)
    }

    private fun getPrivacyRiskScore(): Int {
        val permissionWeight = 10
        val trackerWeight = 5
        val dangerousPermissionCount = countDangerousPermissions(permissionList)
        val trackerCount = trackerList.size
        val rawScore =
            (dangerousPermissionCount * permissionWeight) + (trackerCount * trackerWeight)
        val maxPermissions = 20
        val maxTrackers = 20

        val maxRawScore = (maxPermissions * permissionWeight) + (maxTrackers * trackerWeight)

        Log.e(
            "TAG",
            "getPrivacyRiskScore: $dangerousPermissionCount $trackerCount $rawScore $maxRawScore",
        )

        val normalizedScore = (rawScore.toDouble() / maxRawScore) * 300

        return normalizedScore.toInt().coerceIn(0, 300)
    }

    private fun registerClickListeners() {
        binding.cardviewPermission.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, binding.tvPermissionListLabel.top)
            binding.tvPermissionListLabel.setTextColor(Color.YELLOW)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvPermissionListLabel.setTextColor(Color.WHITE)
            }, 2000)

        }

        binding.cardviewTracker.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, binding.tvTrackerListLabel.top)
            // logic for highlighting
            binding.tvTrackerListLabel.setTextColor(Color.YELLOW)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvTrackerListLabel.setTextColor(Color.WHITE)
            }, 2000)
        }

        binding.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun updateAppInfoUI(appInfo: ApplicationInfo) {
        binding.appIcon.setImageDrawable(appInfo.getAppIcon(this))
        binding.tvAppName.text = appInfo.getAppName(this)
        val packageName = appInfo.packageName
        val versionName = this.packageManager.getPackageInfo(packageName, 0).versionName
        binding.tvPackageAndVersion.text =
            getString(R.string.app_package_and_version, packageName, versionName)

    }

    private suspend fun fetchTrackersForApp(selectedPackageName: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url =
                    "https://reports.exodus-privacy.eu.org/en/reports/$selectedPackageName/latest/" // Hypothetical URL
                val document = Jsoup.connect(url).get()
                val trackerList = ArrayList<String>()
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
                trackerList
            } catch (e: Exception) {
                Log.e("TrackerActivity", "Error: ${e.message}", e)
                emptyList()
            }
        }
    }

    private fun fetchPermissionList(selectedPackageName: String): List<String> {
        return try {
            val packageInfo =
                packageManager.getPackageInfo(selectedPackageName, PackageManager.GET_PERMISSIONS)

            packageInfo.requestedPermissions.toList()

        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("PermissionActivity", "Package not found: $selectedPackageName", e)
            return emptyList()
        }
    }


}

