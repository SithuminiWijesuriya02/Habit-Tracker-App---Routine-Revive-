package com.example.labexam3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.labexam3.databinding.ActivityOnboardingBinding
import com.example.labexam3.onboarding.OnboardingFragment
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Onboarding activity to introduce app features to new users
 */
class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupClickListeners()
    }
    
    private fun setupViewPager() {
        viewPager = binding.viewPager
        val adapter = OnboardingPagerAdapter(this)
        viewPager.adapter = adapter
        
        // Setup tab dots indicator
        TabLayoutMediator(binding.tabLayout, viewPager) { _, _ ->
            // Empty implementation - just for dots
        }.attach()
    }
    
    private fun setupClickListeners() {
        binding.nextButton.setOnClickListener {
            if (viewPager.currentItem < 1) {
                viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }
        
        binding.skipButton.setOnClickListener {
            finishOnboarding()
        }
        
        // Update button text based on current page
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.nextButton.text = if (position == 1) "Get Started" else "Next"
            }
        })
    }
    
    private fun finishOnboarding() {
        val authManager = com.example.labexam3.auth.AuthManager(this)
        
        // Check if user is logged in
        if (authManager.isLoggedIn()) {
            // User is logged in - mark onboarding as completed and go to main app
            authManager.markOnboardingCompleted()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not logged in - go to login page
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
    
    private class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingFragment.newInstance(
                    title = "Track Your Wellness Journey",
                    description = "Monitor your daily habits, mood, exercise routines, and hydration goals all in one place. Build healthy habits that stick!",
                    imageRes = R.drawable.onboarding_wellness,
                    backgroundColor = R.color.onboarding_bg_1
                )
                1 -> OnboardingFragment.newInstance(
                    title = "Stay Motivated & Consistent",
                    description = "Get insights with progress charts, set reminders, and celebrate your achievements. Your wellness journey starts here!",
                    imageRes = R.drawable.onboarding_progress,
                    backgroundColor = R.color.onboarding_bg_2
                )
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
