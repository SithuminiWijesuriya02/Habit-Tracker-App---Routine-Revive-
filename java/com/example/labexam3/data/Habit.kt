package com.example.labexam3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room Entity representing a daily habit
 * @param id Unique identifier for the habit (Primary Key)
 * @param name Name of the habit
 * @param description Optional description
 * @param isCompleted Whether the habit is completed today
 * @param createdAt When the habit was created
 * @param completedDates List of dates when the habit was completed
 * @param userEmail Email of the user who owns this habit
 */
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val description: String = "",
    var isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val completedDates: MutableList<String> = mutableListOf(),
    val userEmail: String = ""
) {

    fun getWeeklyProgress(): Float {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val calendar = java.util.Calendar.getInstance()
        
        var completedDays = 0
        for (i in 0..6) {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(calendar.time)
            if (completedDates.contains(date)) {
                completedDays++
            }
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -1)
        }
        
        return if (completedDays == 0) 0f else (completedDays.toFloat() / 7f) * 100f
    }

    fun markCompleted() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        if (!completedDates.contains(today)) {
            completedDates.add(today)
        }
        isCompleted = true
    }
    
    /**
     * Mark habit as not completed for today
     */
    fun markNotCompleted() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        completedDates.remove(today)
        isCompleted = false
    }
}





