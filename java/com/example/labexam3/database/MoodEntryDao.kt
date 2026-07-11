package com.example.labexam3.database

import androidx.room.*
import com.example.labexam3.data.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {
    
    /**
     * Get all mood entries for a specific user
     * Returns a Flow for reactive updates
     */
    @Query("SELECT * FROM mood_entries WHERE userEmail = :userEmail ORDER BY dateTime DESC")
    fun getAllMoodEntries(userEmail: String): Flow<List<MoodEntry>>
    
    /**
     * Get all mood entries for a specific user (non-reactive)
     */
    @Query("SELECT * FROM mood_entries WHERE userEmail = :userEmail ORDER BY dateTime DESC")
    suspend fun getAllMoodEntriesSync(userEmail: String): List<MoodEntry>
    
    /**
     * Get a specific mood entry by ID
     */
    @Query("SELECT * FROM mood_entries WHERE id = :moodEntryId")
    suspend fun getMoodEntryById(moodEntryId: String): MoodEntry?
    
    /**
     * Insert a new mood entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntry)
    
    /**
     * Insert multiple mood entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntries(moodEntries: List<MoodEntry>)
    
    /**
     * Update an existing mood entry
     */
    @Update
    suspend fun updateMoodEntry(moodEntry: MoodEntry)
    
    /**
     * Delete a mood entry
     */
    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntry)
    
    /**
     * Delete a mood entry by ID
     */
    @Query("DELETE FROM mood_entries WHERE id = :moodEntryId")
    suspend fun deleteMoodEntryById(moodEntryId: String)
    
    /**
     * Delete all mood entries for a user
     */
    @Query("DELETE FROM mood_entries WHERE userEmail = :userEmail")
    suspend fun deleteAllMoodEntries(userEmail: String)
    
    /**
     * Get mood entries count for a user
     */
    @Query("SELECT COUNT(*) FROM mood_entries WHERE userEmail = :userEmail")
    suspend fun getMoodEntriesCount(userEmail: String): Int
    
    /**
     * Get recent mood entries (last N entries)
     */
    @Query("SELECT * FROM mood_entries WHERE userEmail = :userEmail ORDER BY dateTime DESC LIMIT :limit")
    suspend fun getRecentMoodEntries(userEmail: String, limit: Int): List<MoodEntry>
}
