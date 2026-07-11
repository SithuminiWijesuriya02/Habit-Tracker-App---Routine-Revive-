package com.example.labexam3.data

import android.content.Context
import android.content.SharedPreferences
import com.example.labexam3.repository.WellnessRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

/**
 * DataManager - Enhanced data persistence and management system
 * 
 * This class demonstrates advanced Android data management techniques:
 * - Room Database (SQLite ORM) for structured data persistence
 * - SharedPreferences for lightweight settings storage
 * - Repository pattern for clean architecture
 * - Coroutines for asynchronous database operations
 * - JSON serialization using Gson for backup/restore
 * - Comprehensive error handling and logging
 * - Data validation and sanitization
 * - Backup and restore functionality
 * - Daily data reset automation
 * 
 * Features:
 * 1. Habit Management: CRUD operations with completion tracking
 * 2. Mood Entry Management: Emoji-based mood logging with timestamps
 * 3. Exercise Management: Workout tracking with types and metrics
 * 4. Hydration Management: Water intake tracking with goals
 * 5. Data Export/Import: JSON-based backup system
 * 6. Error Recovery: Automatic backup creation and restoration
 * 
 * Data Models Managed:
 * - Habit: Daily wellness habits with completion tracking
 * - MoodEntry: Mood logs with emoji, notes, and timestamps
 * - Exercise: Workout routines with duration and calories
 * - HydrationData: Water intake tracking with reminders
 * 
 * This implementation ensures data integrity and provides robust
 * persistence for the wellness tracking application using Room ORM.
 * 
 * @param context Application context for database and SharedPreferences access
 * @author Lab Exam 4 Implementation - Enhanced with Room Database
 * @version 2.0
 */
class DataManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("wellness_tracker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val repository = WellnessRepository(context)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val KEY_HABITS = "habits"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_DATA = "hydration_data"
        private const val KEY_EXERCISES = "exercises"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
    }
    
    // Get user-specific key
    private fun getUserKey(baseKey: String): String {
        val authManager = com.example.labexam3.auth.AuthManager(context)
        val userEmail = authManager.getCurrentUserEmail()
        val key = if (userEmail.isNotEmpty()) {
            "${baseKey}_$userEmail"
        } else {
            baseKey
        }
        android.util.Log.d("DataManager", "getUserKey: baseKey=$baseKey, userEmail=$userEmail, finalKey=$key")
        return key
    }
    
    // Habit management - Enhanced with Room Database
    fun saveHabits(habits: List<Habit>) {
        try {
            val userEmail = getCurrentUserEmail()
            val habitsWithUser = habits.map { it.copy(userEmail = userEmail) }
            
            // Save to Room database asynchronously
            coroutineScope.launch {
                repository.insertHabits(habitsWithUser)
            }
            
            android.util.Log.d("DataManager", "Saved ${habits.size} habits to Room database")
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error saving habits: ${e.message}")
        }
    }
    
    fun getHabits(): List<Habit> {
        return try {
            val userEmail = getCurrentUserEmail()
            
            // Get from Room database synchronously
            val habits = runBlocking {
                repository.getAllHabitsSync(userEmail)
            }
            
            android.util.Log.d("DataManager", "Loaded ${habits.size} habits from Room database")
            
            if (habits.isNotEmpty()) {
                habits
            } else {
                // First time user - provide default habits
                val defaultHabits = getDefaultHabits()
                android.util.Log.d("DataManager", "No data found, creating ${defaultHabits.size} default habits")
                saveHabits(defaultHabits)
                defaultHabits
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading habits: ${e.message}", e)
            // Return default habits on error
            val defaultHabits = getDefaultHabits()
            saveHabits(defaultHabits)
            defaultHabits
        }
    }
    
    private fun getCurrentUserEmail(): String {
        val authManager = com.example.labexam3.auth.AuthManager(context)
        return authManager.getCurrentUserEmail().ifEmpty { "default_user" }
    }
    
    private fun getDefaultHabits(): List<Habit> {
        return listOf(
            Habit(
                id = "1",
                name = "Drink 8 Glasses of Water",
                description = "Stay hydrated throughout the day",
                isCompleted = false
            ),
            Habit(
                id = "2", 
                name = "Meditate for 10 Minutes",
                description = "Practice mindfulness and relaxation",
                isCompleted = false
            ),
            Habit(
                id = "3",
                name = "Walk 10,000 Steps",
                description = "Get your daily steps in",
                isCompleted = false
            ),
            Habit(
                id = "4",
                name = "Read for 30 Minutes",
                description = "Expand your knowledge and relax",
                isCompleted = false
            ),
            Habit(
                id = "5",
                name = "Take Vitamins",
                description = "Support your health with supplements",
                isCompleted = false
            ),
            Habit(
                id = "6",
                name = "Get 8 Hours of Sleep",
                description = "Prioritize quality rest",
                isCompleted = false
            ),
            Habit(
                id = "7",
                name = "Practice Gratitude",
                description = "Write down 3 things you're grateful for",
                isCompleted = false
            ),
            Habit(
                id = "8",
                name = "Eat 5 Servings of Fruits/Veggies",
                description = "Nourish your body with healthy foods",
                isCompleted = false
            )
        )
    }
    
    fun addHabit(habit: Habit) {
        if (habit.name.isBlank()) {
            android.util.Log.w("DataManager", "Attempted to add habit with empty name")
            return
        }
        val userEmail = getCurrentUserEmail()
        
        // Generate sequential ID if the habit has a UUID
        val habitId = if (habit.id.contains("-")) {
            // It's a UUID, generate sequential ID
            val existingHabits = getHabits()
            val maxId = existingHabits.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
            (maxId + 1).toString()
        } else {
            habit.id
        }
        
        val habitWithUser = habit.copy(id = habitId, userEmail = userEmail)
        
        runBlocking {
            repository.insertHabit(habitWithUser)
        }
        android.util.Log.d("DataManager", "Added habit: ${habit.name} with ID: $habitId")
    }
    
    fun updateHabit(updatedHabit: Habit) {
        if (updatedHabit.name.isBlank()) {
            android.util.Log.w("DataManager", "Attempted to update habit with empty name")
            return
        }
        val userEmail = getCurrentUserEmail()
        val habitWithUser = updatedHabit.copy(userEmail = userEmail)
        
        runBlocking {
            repository.updateHabit(habitWithUser)
        }
        android.util.Log.d("DataManager", "Updated habit: ${updatedHabit.name}")
    }
    
    fun deleteHabit(habitId: String) {
        runBlocking {
            repository.deleteHabitById(habitId)
        }
        android.util.Log.d("DataManager", "Deleted habit: $habitId")
    }
    
    // Mood entry management - Enhanced with Room Database
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        try {
            val userEmail = getCurrentUserEmail()
            val entriesWithUser = moodEntries.map { it.copy(userEmail = userEmail) }
            
            coroutineScope.launch {
                repository.insertMoodEntries(entriesWithUser)
            }
            
            android.util.Log.d("DataManager", "Saved ${moodEntries.size} mood entries to Room database")
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error saving mood entries: ${e.message}")
        }
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        return try {
            val userEmail = getCurrentUserEmail()
            
            runBlocking {
                repository.getAllMoodEntriesSync(userEmail)
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading mood entries: ${e.message}")
            emptyList()
        }
    }
    
    private fun getDefaultMoodEntries(): List<MoodEntry> {
        val calendar = java.util.Calendar.getInstance()
        return listOf(
            MoodEntry(
                id = "1",
                emoji = "😊",
                note = "Felt productive after finishing my tasks!",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -1) }.time
            ),
            MoodEntry(
                id = "2",
                emoji = "😐",
                note = "Relaxed day at home, nothing special",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -2) }.time
            ),
            MoodEntry(
                id = "3",
                emoji = "😔",
                note = "Feeling a bit stressed about work deadlines",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -3) }.time
            ),
            MoodEntry(
                id = "4",
                emoji = "🤩",
                note = "Amazing workout session! Feeling energized",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -4) }.time
            ),
            MoodEntry(
                id = "5",
                emoji = "😌",
                note = "Peaceful meditation session this morning",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -5) }.time
            ),
            MoodEntry(
                id = "6",
                emoji = "😰",
                note = "Busy day with lots of meetings, feeling overwhelmed",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -6) }.time
            ),
            MoodEntry(
                id = "7",
                emoji = "😴",
                note = "Tired after a long day, need more sleep",
                dateTime = calendar.apply { add(java.util.Calendar.DAY_OF_MONTH, -7) }.time
            )
        )
    }
    
    fun addMoodEntry(moodEntry: MoodEntry) {
        val userEmail = getCurrentUserEmail()
        
        // Generate sequential ID if the mood entry has a UUID
        val entryId = if (moodEntry.id.contains("-")) {
            // It's a UUID, generate sequential ID
            val existingEntries = getMoodEntries()
            val maxId = existingEntries.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
            (maxId + 1).toString()
        } else {
            moodEntry.id
        }
        
        val entryWithUser = moodEntry.copy(id = entryId, userEmail = userEmail)
        
        runBlocking {
            repository.insertMoodEntry(entryWithUser)
        }
        android.util.Log.d("DataManager", "Added mood entry with ID: $entryId")
    }
    
    fun updateMoodEntry(updatedMoodEntry: MoodEntry) {
        val userEmail = getCurrentUserEmail()
        val entryWithUser = updatedMoodEntry.copy(userEmail = userEmail)
        
        coroutineScope.launch {
            repository.updateMoodEntry(entryWithUser)
        }
    }
    
    fun deleteMoodEntry(moodEntryId: String) {
        coroutineScope.launch {
            repository.deleteMoodEntryById(moodEntryId)
        }
    }
    
    // Hydration data management - Enhanced with Room Database
    fun saveHydrationData(hydrationData: HydrationData) {
        try {
            val userEmail = getCurrentUserEmail()
            val dataWithUser = hydrationData.copy(userEmail = userEmail)
            
            // Use runBlocking to ensure data is saved immediately
            runBlocking {
                repository.insertHydrationData(dataWithUser)
            }
            
            android.util.Log.d("DataManager", "Saved hydration data to Room database")
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error saving hydration data: ${e.message}")
        }
    }
    
    fun getHydrationData(): HydrationData {
        return try {
            val userEmail = getCurrentUserEmail()
            
            val data = runBlocking {
                repository.getHydrationDataSync(userEmail)
            }
            
            if (data != null) {
                data
            } else {
                // Create default hydration data and save it
                val defaultData = HydrationData(userEmail = userEmail)
                runBlocking {
                    repository.insertHydrationData(defaultData)
                }
                android.util.Log.d("DataManager", "Created default hydration data for user: $userEmail")
                defaultData
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading hydration data: ${e.message}")
            val defaultData = HydrationData(userEmail = getCurrentUserEmail())
            runBlocking {
                repository.insertHydrationData(defaultData)
            }
            defaultData
        }
    }
    
    fun updateHydrationData(updatedData: HydrationData) {
        try {
            val userEmail = getCurrentUserEmail()
            val dataWithUser = updatedData.copy(userEmail = userEmail)
            
            // Use runBlocking to ensure data is saved before returning
            runBlocking {
                repository.updateHydrationData(dataWithUser)
            }
            
            android.util.Log.d("DataManager", "Updated hydration data: glasses=${dataWithUser.glassesToday}")
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error updating hydration data: ${e.message}")
        }
    }
    
    // Daily reset functionality - Enhanced with Room Database
    fun checkAndResetDailyData() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(Date())
        val lastResetDate = sharedPreferences.getString(getUserKey(KEY_LAST_RESET_DATE), "")
        
        if (lastResetDate != today) {
            val userEmail = getCurrentUserEmail()
            
            coroutineScope.launch {
                // Reset hydration data
                repository.resetDailyCount(userEmail)
                
                // Reset habit completion status
                repository.resetAllHabitsCompletion(userEmail)
                
                // Reset exercise completion status
                repository.resetAllExercisesCompletion(userEmail)
            }
            
            // Update last reset date
            sharedPreferences.edit().putString(getUserKey(KEY_LAST_RESET_DATE), today).apply()
            android.util.Log.d("DataManager", "Daily data reset completed for $today")
        }
    }
    
    // Exercise management - Enhanced with Room Database
    fun saveExercises(exercises: List<Exercise>) {
        try {
            val userEmail = getCurrentUserEmail()
            val exercisesWithUser = exercises.map { it.copy(userEmail = userEmail) }
            
            coroutineScope.launch {
                repository.insertExercises(exercisesWithUser)
            }
            
            android.util.Log.d("DataManager", "Saved ${exercises.size} exercises to Room database")
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error saving exercises: ${e.message}")
        }
    }
    
    fun getExercises(): List<Exercise> {
        return try {
            val userEmail = getCurrentUserEmail()
            
            val exercises = runBlocking {
                repository.getAllExercisesSync(userEmail)
            }
            
            if (exercises.isNotEmpty()) {
                exercises
            } else {
                // First time user - provide default exercises
                val defaultExercises = getDefaultExercises()
                saveExercises(defaultExercises)
                defaultExercises
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error loading exercises: ${e.message}")
            val defaultExercises = getDefaultExercises()
            saveExercises(defaultExercises)
            defaultExercises
        }
    }
    
    private fun getDefaultExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = "1",
                name = "Morning Run",
                type = ExerciseType.CARDIO,
                duration = 30,
                caloriesBurned = 250,
                isCompleted = false,
                notes = "Great weather for running!"
            ),
            Exercise(
                id = "2",
                name = "Strength Training",
                type = ExerciseType.STRENGTH,
                duration = 45,
                caloriesBurned = 300,
                isCompleted = false,
                notes = "Focus on upper body today"
            ),
            Exercise(
                id = "3",
                name = "Yoga Flow",
                type = ExerciseType.YOGA,
                duration = 20,
                caloriesBurned = 100,
                isCompleted = false,
                notes = "Perfect for relaxation"
            ),
            Exercise(
                id = "4",
                name = "Basketball",
                type = ExerciseType.SPORTS,
                duration = 60,
                caloriesBurned = 400,
                isCompleted = false,
                notes = "Playing with friends"
            ),
            Exercise(
                id = "5",
                name = "Stretching",
                type = ExerciseType.FLEXIBILITY,
                duration = 15,
                caloriesBurned = 50,
                isCompleted = false,
                notes = "Morning routine"
            )
        )
    }
    
    fun addExercise(exercise: Exercise) {
        if (exercise.name.isBlank() || exercise.duration <= 0) {
            android.util.Log.w("DataManager", "Attempted to add exercise with invalid data")
            return
        }
        val userEmail = getCurrentUserEmail()
        
        // Generate sequential ID if the exercise has a UUID
        val exerciseId = if (exercise.id.contains("-")) {
            // It's a UUID, generate sequential ID
            val existingExercises = getExercises()
            val maxId = existingExercises.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
            (maxId + 1).toString()
        } else {
            exercise.id
        }
        
        val exerciseWithUser = exercise.copy(id = exerciseId, userEmail = userEmail)
        
        runBlocking {
            repository.insertExercise(exerciseWithUser)
        }
        android.util.Log.d("DataManager", "Added exercise: ${exercise.name} with ID: $exerciseId")
    }
    
    fun updateExercise(updatedExercise: Exercise) {
        val userEmail = getCurrentUserEmail()
        val exerciseWithUser = updatedExercise.copy(userEmail = userEmail)
        
        runBlocking {
            repository.updateExercise(exerciseWithUser)
        }
        android.util.Log.d("DataManager", "Updated exercise: ${updatedExercise.name}")
    }
    
    fun deleteExercise(exerciseId: String) {
        runBlocking {
            repository.deleteExerciseById(exerciseId)
        }
        android.util.Log.d("DataManager", "Deleted exercise: $exerciseId")
    }
    
    // Clear all data - Enhanced with Room Database
    fun clearAllData() {
        val userEmail = getCurrentUserEmail()
        
        coroutineScope.launch {
            repository.deleteAllHabits(userEmail)
            repository.deleteAllMoodEntries(userEmail)
            repository.deleteAllExercises(userEmail)
            repository.deleteHydrationData(userEmail)
        }
        
        sharedPreferences.edit().clear().apply()
        android.util.Log.d("DataManager", "All data cleared for user: $userEmail")
    }
    
    // Export data as JSON string
    fun exportData(): String {
        return try {
        val data = mapOf(
            "habits" to getHabits(),
            "moodEntries" to getMoodEntries(),
            "hydrationData" to getHydrationData(),
            "exercises" to getExercises(),
                "exportDate" to Date(),
                "appVersion" to "1.0"
            )
            gson.toJson(data)
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error exporting data: ${e.message}")
            "{\"error\": \"Failed to export data\"}"
        }
    }
    
    // Import data from JSON string
    fun importData(jsonData: String): Boolean {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = gson.fromJson(jsonData, type)
            
            // Validate data structure
            if (!data.containsKey("habits") || !data.containsKey("exercises")) {
                android.util.Log.w("DataManager", "Invalid data format for import")
                return false
            }
            
            // Create backup before import
            createBackup()
            
            // Import habits
            data["habits"]?.let { habitsData ->
                val habitsType = object : TypeToken<List<Habit>>() {}.type
                val habits: List<Habit> = gson.fromJson(gson.toJson(habitsData), habitsType)
                saveHabits(habits)
            }
            
            // Import exercises
            data["exercises"]?.let { exercisesData ->
                val exercisesType = object : TypeToken<List<Exercise>>() {}.type
                val exercises: List<Exercise> = gson.fromJson(gson.toJson(exercisesData), exercisesType)
                saveExercises(exercises)
            }
            
            // Import mood entries if available
            data["moodEntries"]?.let { moodData ->
                val moodType = object : TypeToken<List<MoodEntry>>() {}.type
                val moodEntries: List<MoodEntry> = gson.fromJson(gson.toJson(moodData), moodType)
                saveMoodEntries(moodEntries)
            }
            
            // Import hydration data if available
            data["hydrationData"]?.let { hydrationData ->
                val hydration: HydrationData = gson.fromJson(gson.toJson(hydrationData), HydrationData::class.java)
                saveHydrationData(hydration)
            }
            
            android.util.Log.i("DataManager", "Data imported successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error importing data: ${e.message}")
            restoreBackup()
            false
        }
    }
    
    private fun createBackup() {
        try {
            val backupData = exportData()
            sharedPreferences.edit().putString("backup_data", backupData).apply()
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error creating backup: ${e.message}")
        }
    }
    
    private fun restoreBackup() {
        try {
            val backupData = sharedPreferences.getString("backup_data", null)
            if (backupData != null) {
                importData(backupData)
                android.util.Log.i("DataManager", "Backup restored successfully")
            }
        } catch (e: Exception) {
            android.util.Log.e("DataManager", "Error restoring backup: ${e.message}")
        }
    }
}
