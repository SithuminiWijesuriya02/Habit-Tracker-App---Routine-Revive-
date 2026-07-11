package com.example.labexam3.database

import androidx.room.*
import com.example.labexam3.data.HydrationData
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {
    
    /**
     * Get hydration data for a specific user
     * Returns a Flow for reactive updates
     */
    @Query("SELECT * FROM hydration_data WHERE userEmail = :userEmail LIMIT 1")
    fun getHydrationData(userEmail: String): Flow<HydrationData?>
    
    /**
     * Get hydration data for a specific user (non-reactive)
     */
    @Query("SELECT * FROM hydration_data WHERE userEmail = :userEmail LIMIT 1")
    suspend fun getHydrationDataSync(userEmail: String): HydrationData?
    
    /**
     * Insert or update hydration data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydrationData(hydrationData: HydrationData)
    
    /**
     * Update hydration data
     */
    @Update
    suspend fun updateHydrationData(hydrationData: HydrationData)
    
    /**
     * Delete hydration data for a user
     */
    @Query("DELETE FROM hydration_data WHERE userEmail = :userEmail")
    suspend fun deleteHydrationData(userEmail: String)
    
    /**
     * Reset daily glasses count for a user
     */
    @Query("UPDATE hydration_data SET glassesToday = 0 WHERE userEmail = :userEmail")
    suspend fun resetDailyCount(userEmail: String)
    
    /**
     * Increment glasses count for a user
     */
    @Query("UPDATE hydration_data SET glassesToday = glassesToday + 1 WHERE userEmail = :userEmail")
    suspend fun incrementGlasses(userEmail: String)
    
    /**
     * Decrement glasses count for a user (if > 0)
     */
    @Query("UPDATE hydration_data SET glassesToday = CASE WHEN glassesToday > 0 THEN glassesToday - 1 ELSE 0 END WHERE userEmail = :userEmail")
    suspend fun decrementGlasses(userEmail: String)
}
