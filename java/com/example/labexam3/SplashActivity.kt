package com.example.labexam3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam3.auth.AuthManager

/**
 * Splash screen activity that shows app logo and checks authentication
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authManager = AuthManager(this)

        // Show splash screen for 2 seconds then navigate
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationAndNavigate()
        }, 2000)
    }

    private fun checkAuthenticationAndNavigate() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstTimeUser = prefs.getBoolean("is_first_time_user", true)
        
        when {
            isFirstTimeUser -> {
                // Brand new user - show onboarding first
                prefs.edit().putBoolean("is_first_time_user", false).apply()
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            authManager.isLoggedIn() -> {
                // User is logged in - go directly to main app
                startActivity(Intent(this, MainActivity::class.java))
            }
            else -> {
                // User is not logged in - go to login page
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }
}


