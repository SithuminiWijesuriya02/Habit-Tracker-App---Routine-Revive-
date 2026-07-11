package com.example.labexam3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.labexam3.data.Exercise
import com.example.labexam3.data.Habit
import com.example.labexam3.data.HydrationData
import com.example.labexam3.data.MoodEntry

@Database(
    entities = [
        Habit::class,
        MoodEntry::class,
        Exercise::class,
        HydrationData::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WellnessDatabase : RoomDatabase() {
    
    /**
     * Get HabitDao for habit operations
     */
    abstract fun habitDao(): HabitDao
    
    /**
     * Get MoodEntryDao for mood entry operations
     */
    abstract fun moodEntryDao(): MoodEntryDao
    
    /**
     * Get ExerciseDao for exercise operations
     */
    abstract fun exerciseDao(): ExerciseDao
    
    /**
     * Get HydrationDao for hydration data operations
     */
    abstract fun hydrationDao(): HydrationDao
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: WellnessDatabase? = null
        
        /**
         * Get database instance (Singleton)
         */
        fun getDatabase(context: Context): WellnessDatabase {
            // If instance already exists, return it
            return INSTANCE ?: synchronized(this) {
                // Create database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WellnessDatabase::class.java,
                    "wellness_database"
                )
                    .fallbackToDestructiveMigration() // For development - removes data on schema changes
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Close database instance (for testing)
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
