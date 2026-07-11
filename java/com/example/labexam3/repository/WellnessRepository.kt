package com.example.labexam3.repository

import android.content.Context
import com.example.labexam3.data.Exercise
import com.example.labexam3.data.Habit
import com.example.labexam3.data.HydrationData
import com.example.labexam3.data.MoodEntry
import com.example.labexam3.database.WellnessDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Repository layer for Wellness Tracker App
 * 
 * This class acts as a single source of truth for data access.
 * It abstracts the data sources (Room database) from the rest of the app.
 * 
 * Benefits of Repository Pattern:
 * - Separation of concerns
 * - Easier testing
 * - Centralized data access logic
 * - Can easily switch between data sources (local/remote)
 */
class WellnessRepository(context: Context) {
    
    private val database = WellnessDatabase.getDatabase(context)
    private val habitDao = database.habitDao()
    private val moodEntryDao = database.moodEntryDao()
    private val exerciseDao = database.exerciseDao()
    private val hydrationDao = database.hydrationDao()
    
    // ==================== Habit Operations ====================
    
    /**
     * Get all habits for a user (reactive)
     */
    fun getAllHabits(userEmail: String): Flow<List<Habit>> {
        return habitDao.getAllHabits(userEmail)
    }
    
    /**
     * Get all habits for a user (non-reactive)
     */
    suspend fun getAllHabitsSync(userEmail: String): List<Habit> {
        return habitDao.getAllHabitsSync(userEmail)
    }
    
    /**
     * Get a specific habit by ID
     */
    suspend fun getHabitById(habitId: String): Habit? {
        return habitDao.getHabitById(habitId)
    }
    
    /**
     * Insert a new habit
     */
    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }
    
    /**
     * Insert multiple habits
     */
    suspend fun insertHabits(habits: List<Habit>) {
        habitDao.insertHabits(habits)
    }
    
    /**
     * Update an existing habit
     */
    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }
    
    /**
     * Delete a habit
     */
    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }
    
    /**
     * Delete a habit by ID
     */
    suspend fun deleteHabitById(habitId: String) {
        habitDao.deleteHabitById(habitId)
    }
    
    /**
     * Delete all habits for a user
     */
    suspend fun deleteAllHabits(userEmail: String) {
        habitDao.deleteAllHabits(userEmail)
    }
    
    /**
     * Get completed habits count
     */
    suspend fun getCompletedHabitsCount(userEmail: String): Int {
        return habitDao.getCompletedHabitsCount(userEmail)
    }
    
    /**
     * Reset all habits completion status
     */
    suspend fun resetAllHabitsCompletion(userEmail: String) {
        habitDao.resetAllHabitsCompletion(userEmail)
    }
    
    // ==================== Mood Entry Operations ====================
    
    /**
     * Get all mood entries for a user (reactive)
     */
    fun getAllMoodEntries(userEmail: String): Flow<List<MoodEntry>> {
        return moodEntryDao.getAllMoodEntries(userEmail)
    }
    
    /**
     * Get all mood entries for a user (non-reactive)
     */
    suspend fun getAllMoodEntriesSync(userEmail: String): List<MoodEntry> {
        return moodEntryDao.getAllMoodEntriesSync(userEmail)
    }
    
    /**
     * Get a specific mood entry by ID
     */
    suspend fun getMoodEntryById(moodEntryId: String): MoodEntry? {
        return moodEntryDao.getMoodEntryById(moodEntryId)
    }
    
    /**
     * Insert a new mood entry
     */
    suspend fun insertMoodEntry(moodEntry: MoodEntry) {
        moodEntryDao.insertMoodEntry(moodEntry)
    }
    
    /**
     * Insert multiple mood entries
     */
    suspend fun insertMoodEntries(moodEntries: List<MoodEntry>) {
        moodEntryDao.insertMoodEntries(moodEntries)
    }
    
    /**
     * Update an existing mood entry
     */
    suspend fun updateMoodEntry(moodEntry: MoodEntry) {
        moodEntryDao.updateMoodEntry(moodEntry)
    }
    
    /**
     * Delete a mood entry
     */
    suspend fun deleteMoodEntry(moodEntry: MoodEntry) {
        moodEntryDao.deleteMoodEntry(moodEntry)
    }
    
    /**
     * Delete a mood entry by ID
     */
    suspend fun deleteMoodEntryById(moodEntryId: String) {
        moodEntryDao.deleteMoodEntryById(moodEntryId)
    }
    
    /**
     * Delete all mood entries for a user
     */
    suspend fun deleteAllMoodEntries(userEmail: String) {
        moodEntryDao.deleteAllMoodEntries(userEmail)
    }
    
    /**
     * Get mood entries count
     */
    suspend fun getMoodEntriesCount(userEmail: String): Int {
        return moodEntryDao.getMoodEntriesCount(userEmail)
    }
    
    /**
     * Get recent mood entries
     */
    suspend fun getRecentMoodEntries(userEmail: String, limit: Int): List<MoodEntry> {
        return moodEntryDao.getRecentMoodEntries(userEmail, limit)
    }
    
    // ==================== Exercise Operations ====================
    
    /**
     * Get all exercises for a user (reactive)
     */
    fun getAllExercises(userEmail: String): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises(userEmail)
    }
    
    /**
     * Get all exercises for a user (non-reactive)
     */
    suspend fun getAllExercisesSync(userEmail: String): List<Exercise> {
        return exerciseDao.getAllExercisesSync(userEmail)
    }
    
    /**
     * Get a specific exercise by ID
     */
    suspend fun getExerciseById(exerciseId: String): Exercise? {
        return exerciseDao.getExerciseById(exerciseId)
    }
    
    /**
     * Insert a new exercise
     */
    suspend fun insertExercise(exercise: Exercise) {
        exerciseDao.insertExercise(exercise)
    }
    
    /**
     * Insert multiple exercises
     */
    suspend fun insertExercises(exercises: List<Exercise>) {
        exerciseDao.insertExercises(exercises)
    }
    
    /**
     * Update an existing exercise
     */
    suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(exercise)
    }
    
    /**
     * Delete an exercise
     */
    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise)
    }
    
    /**
     * Delete an exercise by ID
     */
    suspend fun deleteExerciseById(exerciseId: String) {
        exerciseDao.deleteExerciseById(exerciseId)
    }
    
    /**
     * Delete all exercises for a user
     */
    suspend fun deleteAllExercises(userEmail: String) {
        exerciseDao.deleteAllExercises(userEmail)
    }
    
    /**
     * Get completed exercises count
     */
    suspend fun getCompletedExercisesCount(userEmail: String): Int {
        return exerciseDao.getCompletedExercisesCount(userEmail)
    }
    
    /**
     * Get total calories burned
     */
    suspend fun getTotalCaloriesBurned(userEmail: String): Int {
        return exerciseDao.getTotalCaloriesBurned(userEmail) ?: 0
    }
    
    /**
     * Reset all exercises completion status
     */
    suspend fun resetAllExercisesCompletion(userEmail: String) {
        exerciseDao.resetAllExercisesCompletion(userEmail)
    }
    
    // ==================== Hydration Operations ====================
    
    /**
     * Get hydration data for a user (reactive)
     */
    fun getHydrationData(userEmail: String): Flow<HydrationData?> {
        return hydrationDao.getHydrationData(userEmail)
    }
    
    /**
     * Get hydration data for a user (non-reactive)
     */
    suspend fun getHydrationDataSync(userEmail: String): HydrationData? {
        return hydrationDao.getHydrationDataSync(userEmail)
    }
    
    /**
     * Insert or update hydration data
     */
    suspend fun insertHydrationData(hydrationData: HydrationData) {
        hydrationDao.insertHydrationData(hydrationData)
    }
    
    /**
     * Update hydration data
     */
    suspend fun updateHydrationData(hydrationData: HydrationData) {
        hydrationDao.updateHydrationData(hydrationData)
    }
    
    /**
     * Delete hydration data for a user
     */
    suspend fun deleteHydrationData(userEmail: String) {
        hydrationDao.deleteHydrationData(userEmail)
    }
    
    /**
     * Reset daily glasses count
     */
    suspend fun resetDailyCount(userEmail: String) {
        hydrationDao.resetDailyCount(userEmail)
    }
    
    /**
     * Increment glasses count
     */
    suspend fun incrementGlasses(userEmail: String) {
        hydrationDao.incrementGlasses(userEmail)
    }
    
    /**
     * Decrement glasses count
     */
    suspend fun decrementGlasses(userEmail: String) {
        hydrationDao.decrementGlasses(userEmail)
    }
}
