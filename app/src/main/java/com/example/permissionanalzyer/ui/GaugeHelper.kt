package com.example.permissionanalzyer.ui

import android.util.Log
import com.example.permissionanalzyer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GaugeHelper(
    private val onRotateDegreeChanged: (Float) -> Unit,
    private val onSweepAngleFirstChartChanged: (Float) -> Unit,
    private val onSweepAngleSecondChartChanged: (Float) -> Unit,
    private val onSweepAngleThirdChartChanged: (Float) -> Unit,
) {
    private var degree = -225f
    var isInProgress = false
        private set

    private var job: Job? = null

    init {
        // Initialize the charts to be fully drawn
        onSweepAngleFirstChartChanged(90f)
        onSweepAngleSecondChartChanged(90f)
        onSweepAngleThirdChartChanged(90f)
    }

    fun startRunning(value: Int) {
        if (!isInProgress) {
            isInProgress = true
            job = CoroutineScope(Dispatchers.Main).launch {
                for (i in 0..value) {
                    degree++
                    Log.e("TAG", "startRunning: $degree")

                    if (degree < 45) {
                        onRotateDegreeChanged(degree)
                    }
                    val (riskLevel, colorResId) = when {
                        degree <= -135 -> Pair("low", R.color.low_green)
                        degree <= -45 -> Pair("moderate", R.color.mid_yellow)
                        degree <= 45 -> Pair("high", R.color.high_red)
                        else -> Pair("low", R.color.low_green)
                    }


                    delay(15)
                }
                isInProgress = false
            }
        }
    }
}
