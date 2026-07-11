package com.example.labexam3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.google.android.material.card.MaterialCardView

/**
 * Home Fragment - Main landing page with navigation to all sections
 */
class HomeFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            mainActivity = activity as MainActivity
            setupNavigationCards(view)
        } catch (e: Exception) {
            android.util.Log.e("HomeFragment", "Error in onViewCreated", e)
        }
    }

    private fun setupNavigationCards(view: View) {
        try {
            // Habits Card
            view.findViewById<MaterialCardView>(R.id.card_habits)?.setOnClickListener {
                mainActivity.navigateToFragment(HabitsFragment(), "Habits")
            }

            // Exercise Card
            view.findViewById<MaterialCardView>(R.id.card_exercise)?.setOnClickListener {
                mainActivity.navigateToFragment(ExerciseFragment(), "Exercise")
            }

            // Mood Journal Card
            view.findViewById<MaterialCardView>(R.id.card_mood)?.setOnClickListener {
                mainActivity.navigateToFragment(MoodJournalFragment(), "Mood Journal")
            }

            // Hydration Card
            view.findViewById<MaterialCardView>(R.id.card_hydration)?.setOnClickListener {
                mainActivity.navigateToFragment(HydrationFragment(), "Hydration")
            }

            // Progress Card
            view.findViewById<MaterialCardView>(R.id.card_progress)?.setOnClickListener {
                mainActivity.navigateToFragment(ProgressFragment(), "Progress")
            }

            // Settings Card
            view.findViewById<MaterialCardView>(R.id.card_settings)?.setOnClickListener {
                mainActivity.navigateToFragment(SettingsFragment(), "Settings")
            }
        } catch (e: Exception) {
            android.util.Log.e("HomeFragment", "Error setting up navigation cards", e)
        }
    }
}
