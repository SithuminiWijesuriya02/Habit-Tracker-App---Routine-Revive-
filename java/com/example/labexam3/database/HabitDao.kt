package com.example.labexam3.database

import androidx.room.*
import com.example.labexam3.data.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    
    /**
     * Get all habits for a specific user
     * Returns a Flow for reactive updates
     */
    @Query("SELECT * FROM habits WHERE userEmail = :userEmail ORDER BY createdAt DESC")
    fun getAllHabits(userEmail: String): Flow<List<Habit>>
    
    /** Get all habits for a specific user (non-reactive)
     */
    @Query("SELECT * FROM habits WHERE userEmail = :userEmail ORDER BY createdAt DESC")
    suspend fun getAllHabitsSync(userEmail: String): List<Habit>
    
    /**Get a specific habit by ID
     */
    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: String): Habit?
    
    /**Insert a new habit
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)
    
    /**Insert multiple habits
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<Habit>)
    
    /**
     * Update an existing habit
     */
    @Update
    suspend fun updateHabit(habit: Habit)
    
    /**Delete a habit
     */
    @Delete
    suspend fun deleteHabit(habit: Habit)
    
    /**Delete a habit by ID
     */
    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: String)
    
    /**
     * Delete all habits for a user
     */
    @Query("DELETE FROM habits WHERE userEmail = :userEmail")
    suspend fun deleteAllHabits(userEmail: String)
    
    /**Get completed habits count for a user
     */
    @Query("SELECT COUNT(*) FROM habits WHERE userEmail = :userEmail AND isCompleted = 1")
    suspend fun getCompletedHabitsCount(userEmail: String): Int
    
    /**
     * Reset all habits completion status for a user
     */
    @Query("UPDATE habits SET isCompleted = 0 WHERE userEmail = :userEmail")
    suspend fun resetAllHabitsCompletion(userEmail: String)
}
