package com.example.labexam3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room Entity representing a mood journal entry
 * @param id Unique identifier for the mood entry (Primary Key)
 * @param emoji Emoji representing the mood
 * @param note Optional note about the mood
 * @param dateTime When the mood was logged
 * @param userEmail Email of the user who owns this mood entry
 */
@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey
    val id: String = "",
    val emoji: String = "",
    val note: String = "",
    val dateTime: Date = Date(),
    val userEmail: String = ""
) {
    /**
     * Get formatted date string
     */
    fun getFormattedDate(): String {
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return dateFormat.format(dateTime)
    }
    
    /**
     * Get formatted time string
     */
    fun getFormattedTime(): String {
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return timeFormat.format(dateTime)
    }
    
    /**
     * Get mood description based on emoji
     */
    fun getMoodDescription(): String {
        return when (emoji) {
            "😊" -> "Happy"
            "😢" -> "Sad"
            "😠" -> "Angry"
            "🤩" -> "Excited"
            "😌" -> "Calm"
            "😰" -> "Stressed"
            "😴" -> "Tired"
            "😐" -> "Neutral"
            else -> "Unknown"
        }
    }
}

