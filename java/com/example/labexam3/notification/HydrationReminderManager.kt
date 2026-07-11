package com.example.labexam3.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.labexam3.data.DataManager
import com.example.labexam3.data.HydrationData
import java.util.Calendar

/**
 * Manager class for handling hydration reminder notifications
 */
class HydrationReminderManager(private val context: Context) {
    
    private val dataManager = DataManager(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    companion object {
        private const val REQUEST_CODE = 1001
    }
    
    /**
     * Schedule hydration reminders at specified interval
     */
    fun scheduleReminders(intervalMinutes: Int) {
        cancelReminders()
        
        val intent = Intent(context, HydrationNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, intervalMinutes)
        }
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intervalMinutes * 60 * 1000L, // Convert minutes to milliseconds
            pendingIntent
        )
    }
    
    /**
     * Cancel all hydration reminders
     */
    fun cancelReminders() {
        val intent = Intent(context, HydrationNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
    
    /**
     * Update reminder settings
     */
    fun updateReminderSettings(enabled: Boolean, intervalMinutes: Int) {
        val hydrationData = getHydrationData()
        hydrationData.reminderEnabled = enabled
        hydrationData.reminderIntervalMinutes = intervalMinutes
        
        dataManager.updateHydrationData(hydrationData)
        
        if (enabled) {
            scheduleReminders(intervalMinutes)
        } else {
            cancelReminders()
        }
    }
    
    /**
     * Get current hydration data
     */
    fun getHydrationData(): HydrationData {
        return dataManager.getHydrationData()
    }
}

