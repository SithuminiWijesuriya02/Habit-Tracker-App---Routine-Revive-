package com.example.labexam3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room Entity representing an exercise routine
 * @param id Unique identifier for the exercise (Primary Key)
 * @param name Name of the exercise
 * @param type Type of exercise (cardio, strength, flexibility, etc.)
 * @param duration Duration in minutes
 * @param caloriesBurned Estimated calories burned
 * @param isCompleted Whether the exercise is completed today
 * @param completedAt When the exercise was completed
 * @param notes Optional notes about the exercise
 * @param userEmail Email of the user who owns this exercise
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val type: ExerciseType = ExerciseType.CARDIO,
    val duration: Int = 0, // in minutes
    val caloriesBurned: Int = 0,
    var isCompleted: Boolean = false,
    val completedAt: Date? = null,
    val notes: String = "",
    val userEmail: String = ""
) {
    /**
     * Get formatted duration string
     */
    fun getFormattedDuration(): String {
        return if (duration >= 60) {
            val hours = duration / 60
            val minutes = duration % 60
            if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
        } else {
            "${duration}m"
        }
    }
    
    /**
     * Get exercise type display name
     */
    fun getTypeDisplayName(): String {
        return when (type) {
            ExerciseType.CARDIO -> "Cardio"
            ExerciseType.STRENGTH -> "Strength"
            ExerciseType.FLEXIBILITY -> "Flexibility"
            ExerciseType.YOGA -> "Yoga"
            ExerciseType.SPORTS -> "Sports"
            ExerciseType.DANCE -> "Dance"
        }
    }
    
    /**
     * Get exercise icon resource
     */
    fun getIconResource(): Int {
        return when (type) {
            ExerciseType.CARDIO -> com.example.labexam3.R.drawable.ic_cardio
            ExerciseType.STRENGTH -> com.example.labexam3.R.drawable.ic_strength
            ExerciseType.FLEXIBILITY -> com.example.labexam3.R.drawable.ic_flexibility
            ExerciseType.YOGA -> com.example.labexam3.R.drawable.ic_yoga
            ExerciseType.SPORTS -> com.example.labexam3.R.drawable.ic_sports
            ExerciseType.DANCE -> com.example.labexam3.R.drawable.ic_dance
        }
    }
}

enum class ExerciseType {
    CARDIO, STRENGTH, FLEXIBILITY, YOGA, SPORTS, DANCE
}





