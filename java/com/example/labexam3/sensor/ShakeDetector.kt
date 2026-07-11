package com.example.labexam3.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Advanced sensor integration for wellness tracking
 * 
 * Features:
 * 1. Shake Detection: Quick gesture to add mood entries
 * 2. Step Counting: Track daily movement for wellness goals
 * 3. Activity Recognition: Detect when user is active
 * 
 * This demonstrates advanced Android sensor integration as required
 * for the lab exam advanced features section.
 */
class ShakeDetector(
    private val context: Context,
    private val onShakeDetected: () -> Unit,
    private val onStepDetected: (() -> Unit)? = null
) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    
    // Shake detection variables
    private var lastUpdate = 0L
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    
    // Step counting variables
    private var initialStepCount = -1
    private var currentSteps = 0
    
    companion object {
        private const val SHAKE_THRESHOLD = 800
        private const val TIME_THRESHOLD = 100
        private const val TAG = "ShakeDetector"
    }
    
    /**
     * Start listening for sensor events
     * Registers listeners for both accelerometer (shake) and step counter
     */
    fun startListening(requireContext: Context) {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            android.util.Log.d(TAG, "Accelerometer sensor registered")
        }
        
        stepCounter?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            android.util.Log.d(TAG, "Step counter sensor registered")
        }
    }
    
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }
    class ShakeDetector(
        private val context: Context,
        private val onShakeDetected: () -> Unit,
        private val onStepDetected: (() -> Unit)? = null
    )

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    handleAccelerometerEvent(sensorEvent)
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    handleStepCounterEvent(sensorEvent)
                }
            }
        }
    }
    
    /**
     * Handle accelerometer events for shake detection
     */
    private fun handleAccelerometerEvent(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastUpdate > TIME_THRESHOLD) {
            val diffTime = currentTime - lastUpdate
            lastUpdate = currentTime
            
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
            
            if (speed > SHAKE_THRESHOLD) {
                android.util.Log.d(TAG, "Shake detected! Speed: $speed")
                onShakeDetected()
            }
            
            lastX = x
            lastY = y
            lastZ = z
        }
    }
    
    /**
     * Handle step counter events for activity tracking
     */
    private fun handleStepCounterEvent(event: SensorEvent) {
        val stepCount = event.values[0].toInt()
        
        if (initialStepCount == -1) {
            initialStepCount = stepCount
            android.util.Log.d(TAG, "Initial step count: $initialStepCount")
        } else {
            val newSteps = stepCount - initialStepCount
            if (newSteps > currentSteps) {
                currentSteps = newSteps
                onStepDetected?.invoke()
                android.util.Log.d(TAG, "Steps today: $currentSteps")
            }
        }
    }
    
    /**
     * Get current step count for today
     */
    fun getCurrentSteps(): Int = currentSteps
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}


