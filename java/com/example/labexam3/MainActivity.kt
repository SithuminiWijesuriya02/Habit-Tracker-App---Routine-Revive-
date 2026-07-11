package com.example.labexam3

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.labexam3.auth.AuthManager
import com.example.labexam3.data.DataManager
import com.example.labexam3.fragments.*
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {

    private lateinit var dataManager: DataManager
    private lateinit var authManager: AuthManager

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dataManager = DataManager(this)
        authManager = AuthManager(this)

        setupToolbar()
        setupCustomBottomNav()
        requestNotificationPermission()

        // Reset daily data if needed
        dataManager.checkAndResetDailyData()

        // Load Home by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Routine Revive"
    }

    private fun setupCustomBottomNav() {
        val navHome = findViewById<LinearLayout>(R.id.nav_home)
        val navHabits = findViewById<LinearLayout>(R.id.nav_habits)
        val navExercise = findViewById<LinearLayout>(R.id.nav_exercise)
        val navMood = findViewById<LinearLayout>(R.id.nav_mood)
        val navHydration = findViewById<LinearLayout>(R.id.nav_hydration)
        val navProgress = findViewById<LinearLayout>(R.id.nav_progress)

        navHome.setOnClickListener { loadFragment(HomeFragment()) }
        navHabits.setOnClickListener { loadFragment(HabitsFragment()) }
        navExercise.setOnClickListener { loadFragment(ExerciseFragment()) }
        navMood.setOnClickListener { loadFragment(MoodJournalFragment()) }
        navHydration.setOnClickListener { loadFragment(HydrationFragment()) }
        navProgress.setOnClickListener { loadFragment(ProgressFragment()) }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        
        // Show back button if not on home fragment
        val showBackButton = fragment !is HomeFragment
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
        
        // Hide profile/settings icons when on Profile or Settings page
        invalidateOptionsMenu()
    }
    
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        
        // Hide profile and settings icons when on those pages
        menu?.findItem(R.id.action_profile)?.isVisible = currentFragment !is ProfileFragment
        menu?.findItem(R.id.action_settings)?.isVisible = currentFragment !is SettingsFragment
        
        return super.onPrepareOptionsMenu(menu)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment !is HomeFragment) {
            loadFragment(HomeFragment())
        } else {
            super.onBackPressed()
        }
    }


    fun getDataManager(): DataManager {
        return dataManager
    }

    fun navigateToFragment(fragment: Fragment, title: String) {
        supportActionBar?.title = title
        loadFragment(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Back button clicked - go to home
                loadFragment(HomeFragment())
                true
            }
            R.id.action_profile -> {
                loadFragment(ProfileFragment())
                true
            }
            R.id.action_settings -> {
                loadFragment(SettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
