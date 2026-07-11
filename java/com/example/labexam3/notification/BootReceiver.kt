package com.example.labexam3.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver to restart hydration reminders after device reboot
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Restart hydration reminders if they were enabled
            val hydrationManager = HydrationReminderManager(context)
            val hydrationData = hydrationManager.getHydrationData()
            
            if (hydrationData.reminderEnabled) {
                hydrationManager.scheduleReminders(hydrationData.reminderIntervalMinutes)
            }
        }
    }
}

