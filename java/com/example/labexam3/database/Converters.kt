package com.example.labexam3.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters for Room database
 * Converts complex types to primitive types that SQLite can store
 */
class Converters {
    
    private val gson = Gson()
    
    /**
     * Convert Date to Long (timestamp)
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    
    /**
     * Convert Long (timestamp) to Date
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
    
    /**
     * Convert MutableList<String> to JSON String
     * This also handles List<String> since MutableList is a subtype
     */
    @TypeConverter
    fun fromMutableStringList(list: MutableList<String>?): String? {
        return gson.toJson(list)
    }
    
    /**
     * Convert JSON String to MutableList<String>
     * Room will use this for both List<String> and MutableList<String>
     */
    @TypeConverter
    fun toMutableStringList(json: String?): MutableList<String>? {
        if (json == null) return null
        val type = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(json, type)
    }
}
