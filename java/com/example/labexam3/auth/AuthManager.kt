package com.example.labexam3.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Authentication manager for handling user login state
 */
class AuthManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_REMEMBER_LOGIN = "remember_login"
        private const val KEY_ONBOARDING_PREFIX = "onboarding_completed_"
    }
    
    /**
     * Check if user is currently logged in
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Login user with email and remember preference
     */
    fun login(email: String, rememberLogin: Boolean = true) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            putBoolean(KEY_REMEMBER_LOGIN, rememberLogin)
            apply()
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        val rememberLogin = sharedPreferences.getBoolean(KEY_REMEMBER_LOGIN, false)
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            if (!rememberLogin) {
                putString(KEY_USER_EMAIL, "")
                putBoolean(KEY_REMEMBER_LOGIN, false)
            }
            apply()
        }
    }
    
    /**
     * Get current user email
     */
    fun getCurrentUserEmail(): String {
        return sharedPreferences.getString(KEY_USER_EMAIL, "") ?: ""
    }
    
    /**
     * Check if remember login is enabled
     */
    fun isRememberLoginEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_LOGIN, false)
    }
    
    /**
     * Set remember login preference
     */
    fun setRememberLogin(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_LOGIN, enabled).apply()
    }
    
    /**
     * Check if current user has completed onboarding
     */
    fun hasCompletedOnboarding(): Boolean {
        val email = getCurrentUserEmail()
        if (email.isEmpty()) return true // If no email, skip onboarding
        return sharedPreferences.getBoolean("$KEY_ONBOARDING_PREFIX$email", false)
    }
    
    /**
     * Mark onboarding as completed for current user
     */
    fun markOnboardingCompleted() {
        val email = getCurrentUserEmail()
        if (email.isNotEmpty()) {
            sharedPreferences.edit().apply {
                putBoolean("$KEY_ONBOARDING_PREFIX$email", true)
                apply()
            }
        }
    }
    
    /**
     * Reset onboarding status (for testing)
     */
    fun resetOnboarding() {
        val email = getCurrentUserEmail()
        if (email.isNotEmpty()) {
            sharedPreferences.edit().apply {
                putBoolean("$KEY_ONBOARDING_PREFIX$email", false)
                apply()
            }
        }
    }
}





