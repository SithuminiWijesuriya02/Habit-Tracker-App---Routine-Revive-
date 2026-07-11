-- Useful SQL Queries for Database Inspector
-- Copy and paste these queries into Database Inspector to view formatted data

-- ============================================
-- EXERCISES - View with readable completion date
-- ============================================
SELECT 
    id,
    name,
    type,
    duration,
    caloriesBurned,
    isCompleted,
    datetime(completedAt/1000, 'unixepoch', 'localtime') as completedAt_readable,
    notes,
    userEmail
FROM exercises;

-- ============================================
-- EXERCISES - Only completed exercises with dates
-- ============================================
SELECT 
    id,
    name,
    type,
    duration,
    datetime(completedAt/1000, 'unixepoch', 'localtime') as completed_at,
    userEmail
FROM exercises
WHERE isCompleted = 1 AND completedAt IS NOT NULL
ORDER BY completedAt DESC;

-- ============================================
-- HABITS - View all habits
-- ============================================
SELECT 
    id,
    name,
    description,
    isCompleted,
    datetime(createdAt/1000, 'unixepoch', 'localtime') as created_at,
    completedDates,
    userEmail
FROM habits;

-- ============================================
-- MOOD ENTRIES - View with readable dates
-- ============================================
SELECT 
    id,
    emoji,
    note,
    datetime(dateTime/1000, 'unixepoch', 'localtime') as entry_date,
    userEmail
FROM mood_entries
ORDER BY dateTime DESC;

-- ============================================
-- HYDRATION DATA - View with readable dates
-- ============================================
SELECT 
    userEmail,
    glassesToday,
    dailyGoal,
    datetime(lastUpdated/1000, 'unixepoch', 'localtime') as last_updated,
    reminderEnabled,
    reminderIntervalMinutes
FROM hydration_data;

-- ============================================
-- SUMMARY - Count all data by user
-- ============================================
SELECT 
    'Habits' as data_type,
    COUNT(*) as count,
    userEmail
FROM habits
GROUP BY userEmail

UNION ALL

SELECT 
    'Mood Entries' as data_type,
    COUNT(*) as count,
    userEmail
FROM mood_entries
GROUP BY userEmail

UNION ALL

SELECT 
    'Exercises' as data_type,
    COUNT(*) as count,
    userEmail
FROM exercises
GROUP BY userEmail;

-- ============================================
-- COMPLETED TODAY - All completed items
-- ============================================
SELECT 
    'Habit' as type,
    name,
    'N/A' as completed_time,
    userEmail
FROM habits
WHERE isCompleted = 1

UNION ALL

SELECT 
    'Exercise' as type,
    name,
    datetime(completedAt/1000, 'unixepoch', 'localtime') as completed_time,
    userEmail
FROM exercises
WHERE isCompleted = 1 AND completedAt IS NOT NULL
ORDER BY completed_time DESC;

-- ============================================
-- RECENT ACTIVITY - Last 10 actions
-- ============================================
SELECT 
    'Mood Entry' as activity_type,
    emoji || ' - ' || note as description,
    datetime(dateTime/1000, 'unixepoch', 'localtime') as activity_time,
    userEmail
FROM mood_entries
ORDER BY dateTime DESC
LIMIT 10;

-- ============================================
-- USER STATISTICS - Per user summary
-- ============================================
SELECT 
    h.userEmail,
    COUNT(DISTINCT h.id) as total_habits,
    SUM(CASE WHEN h.isCompleted = 1 THEN 1 ELSE 0 END) as completed_habits,
    COUNT(DISTINCT e.id) as total_exercises,
    SUM(CASE WHEN e.isCompleted = 1 THEN 1 ELSE 0 END) as completed_exercises,
    COUNT(DISTINCT m.id) as total_mood_entries
FROM habits h
LEFT JOIN exercises e ON h.userEmail = e.userEmail
LEFT JOIN mood_entries m ON h.userEmail = m.userEmail
GROUP BY h.userEmail;

-- ============================================
-- EXERCISE STATISTICS - Calories and duration
-- ============================================
SELECT 
    userEmail,
    COUNT(*) as total_exercises,
    SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed_count,
    SUM(duration) as total_minutes,
    SUM(CASE WHEN isCompleted = 1 THEN caloriesBurned ELSE 0 END) as total_calories_burned,
    AVG(duration) as avg_duration
FROM exercises
GROUP BY userEmail;

-- ============================================
-- SEARCH - Find exercises by name
-- ============================================
-- Replace 'Running' with your search term
SELECT 
    id,
    name,
    type,
    duration,
    datetime(completedAt/1000, 'unixepoch', 'localtime') as completed_at
FROM exercises
WHERE name LIKE '%Running%'
ORDER BY completedAt DESC;

-- ============================================
-- DELETE - Remove test data (USE WITH CAUTION!)
-- ============================================
-- Uncomment to delete test exercises
-- DELETE FROM exercises WHERE name LIKE '%Test%';

-- Uncomment to delete all data for a specific user
-- DELETE FROM habits WHERE userEmail = 'test@example.com';
-- DELETE FROM exercises WHERE userEmail = 'test@example.com';
-- DELETE FROM mood_entries WHERE userEmail = 'test@example.com';
-- DELETE FROM hydration_data WHERE userEmail = 'test@example.com';
