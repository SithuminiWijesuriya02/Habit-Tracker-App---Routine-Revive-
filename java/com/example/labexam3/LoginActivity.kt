package com.example.labexam3

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam3.auth.AuthManager
import com.example.labexam3.databinding.ActivityLoginBinding

/**
 * Login activity with modern design matching reference images
 */
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)
        
        authManager = AuthManager(this)
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        findViewById<com.google.android.material.button.MaterialButton>(R.id.login_button).setOnClickListener {
            performLogin()
        }
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.signup_toggle_button).setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        
        findViewById<android.widget.TextView>(R.id.sign_up_text).setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.demo_login_button).setOnClickListener {
            performDemoLogin()
        }
        
        findViewById<android.widget.ImageView>(R.id.back_to_onboarding_button).setOnClickListener {
            startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
        }
    }

    private fun performLogin() {
        val emailEditText = findViewById<android.widget.EditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<android.widget.EditText>(R.id.password_edit_text)
        
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Basic validation
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve stored credentials from SharedPreferences
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val savedEmail = prefs.getString("user_email", null)
        val savedPassword = prefs.getString("user_password", null)

        // Check credentials
        if (email == savedEmail && password == savedPassword) {
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                authManager.login(email, true)
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                
                // Go directly to main app after login
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 800)
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performDemoLogin() {
        // Create demo account if it doesn't exist
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (!prefs.contains("user_email")) {
            prefs.edit().apply {
                putString("user_name", "Demo User")
                putString("user_email", "demo@example.com")
                putString("user_password", "demo123")
                apply()
            }
        }
        
        authManager.login("demo@example.com", true)
        Toast.makeText(this, "Demo login successful!", Toast.LENGTH_SHORT).show()
        
        // Go directly to main app
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


