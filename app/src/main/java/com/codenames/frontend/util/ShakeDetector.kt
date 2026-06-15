package com.codenames.frontend.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit,
) : SensorEventListener {
    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration =
            sqrt(
                (x * x + y * y + z * z).toDouble(),
            )

        if (acceleration > 15) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {}
}
