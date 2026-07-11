package com.example.labexam3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room Entity representing hydration tracking data
 * @param userEmail Email of the user (Primary Key)
 * @param glassesToday Number of glasses consumed today
 * @param dailyGoal Daily goal for glasses of water
 * @param lastUpdated When the data was last updated
 * @param reminderEnabled Whether reminders are enabled
 * @param reminderIntervalMinutes Reminder interval in minutes
 */
@Entity(tableName = "hydration_data")
data class HydrationData(
    @PrimaryKey
    val userEmail: String = "",
    var glassesToday: Int = 0,
    var dailyGoal: Int = 8,
    val lastUpdated: Date = Date(),
    var reminderEnabled: Boolean = false,
    var reminderIntervalMinutes: Int = 60
) {
    /**
     * Get progress percentage towards daily goal
     */
    fun getProgressPercentage(): Float {
        return if (dailyGoal == 0) 0f else (glassesToday.toFloat() / dailyGoal.toFloat()) * 100f
    }
    
    /**
     * Add a glass of water
     */
    fun addGlass() {
        glassesToday++
    }
    
    /**
     * Remove a glass of water (if glassesToday > 0)
     */
    fun removeGlass() {
        if (glassesToday > 0) {
            glassesToday--
        }
    }
    
    /**
     * Check if daily goal is reached
     */
    fun isGoalReached(): Boolean {
        return glassesToday >= dailyGoal
    }
    
    /**
     * Reset daily count (typically called at midnight)
     */
    fun resetDailyCount() {
        glassesToday = 0
    }
}

