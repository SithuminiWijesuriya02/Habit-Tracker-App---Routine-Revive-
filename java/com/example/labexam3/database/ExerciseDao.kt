package com.example.labexam3.database

import androidx.room.*
import com.example.labexam3.data.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    
    /**
     * Get all exercises for a specific user
     * Returns a Flow for reactive updates
     */
    @Query("SELECT * FROM exercises WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getAllExercises(userEmail: String): Flow<List<Exercise>>
    
    /** Get all exercises for a specific user (non-reactive)
     */
    @Query("SELECT * FROM exercises WHERE userEmail = :userEmail ORDER BY id DESC")
    suspend fun getAllExercisesSync(userEmail: String): List<Exercise>
    
    /** Get a specific exercise by ID
     */
    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: String): Exercise?
    
    /** Insert a new exercise
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)
    
    /** Insert multiple exercises
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)
    
    /** Update an existing exercise
     */
    @Update
    suspend fun updateExercise(exercise: Exercise)
    
    /** Delete an exercise
     */
    @Delete
    suspend fun deleteExercise(exercise: Exercise)
    
    /** Delete an exercise by ID
     */
    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: String)
    
    /**
     * Delete all exercises for a user
     */
    @Query("DELETE FROM exercises WHERE userEmail = :userEmail")
    suspend fun deleteAllExercises(userEmail: String)
    
    /**
     * Get completed exercises count for a user
     */
    @Query("SELECT COUNT(*) FROM exercises WHERE userEmail = :userEmail AND isCompleted = 1")
    suspend fun getCompletedExercisesCount(userEmail: String): Int
    
    /**
     * Get total calories burned for a user
     */
    @Query("SELECT SUM(caloriesBurned) FROM exercises WHERE userEmail = :userEmail AND isCompleted = 1")
    suspend fun getTotalCaloriesBurned(userEmail: String): Int?
    
    /**
     * Reset all exercises completion status for a user
     */
    @Query("UPDATE exercises SET isCompleted = 0 WHERE userEmail = :userEmail")
    suspend fun resetAllExercisesCompletion(userEmail: String)
}
