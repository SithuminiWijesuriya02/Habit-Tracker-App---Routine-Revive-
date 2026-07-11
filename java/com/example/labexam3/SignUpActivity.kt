package com.example.labexam3

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam3.auth.AuthManager
import com.example.labexam3.databinding.ActivitySignUpBinding

/**
 * Sign up activity for new user registration
 */
class SignUpActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_new)
        
        authManager = AuthManager(this)
        setupClickListeners()
    }
    
    private fun setupUI() {
        binding.apply {
            // Set app name and tagline
            appNameText.text = "Routine Revive"
            taglineText.text = "Your daily dose of well-being"
            
            // Set welcome message
            welcomeText.text = "Create Account"
            
            // Set up input fields
            nameInputLayout.hint = "Full Name"
            emailInputLayout.hint = "Email Address"
            passwordInputLayout.hint = "Password"
            confirmPasswordInputLayout.hint = "Confirm Password"
            
            // Set up buttons
            signUpButton.text = "Sign Up"
            loginText.text = "Already have an account? Login"
        }
    }
    
    private fun setupClickListeners() {
        findViewById<com.google.android.material.button.MaterialButton>(R.id.sign_up_button).setOnClickListener {
            performSignUp()
        }
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.login_toggle_button).setOnClickListener {
            finish() // Go back to login
        }
        
        findViewById<android.widget.TextView>(R.id.login_text).setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun performSignUp() {
        val usernameEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.username_edit_text)
        val emailEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password_edit_text)
        val confirmPasswordEditText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.confirm_password_edit_text)
        
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validation
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if user already exists
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val existingEmail = prefs.getString("user_email", null)
        
        if (existingEmail == email) {
            Toast.makeText(this, "An account with this email already exists", Toast.LENGTH_SHORT).show()
            return
        }

        // Save user credentials with username
        prefs.edit().apply {
            putString("user_name_$email", username)
            putString("user_email", email)
            putString("user_password", password)
            apply()
        }

        // Log the user in
        authManager.login(email, true)

        // Simulate account creation delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            
            // New users always see onboarding
            val intent = Intent(this, OnboardingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 800)
    }
}