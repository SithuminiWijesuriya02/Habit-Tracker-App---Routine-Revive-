package com.example.labexam3.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.labexam3.LoginActivity
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.auth.AuthManager
import com.google.android.material.card.MaterialCardView

/**
 * Fragment for app settings and data management
 */
class SettingsFragment : Fragment() {
    
    private lateinit var clearDataCard: MaterialCardView
    private lateinit var exportDataCard: MaterialCardView
    private lateinit var mainActivity: MainActivity
    private lateinit var authManager: AuthManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mainActivity = activity as MainActivity
        authManager = AuthManager(requireContext())
        initializeViews(view)
        setupListeners()
    }
    
    private fun initializeViews(view: View) {
        clearDataCard = view.findViewById(R.id.clear_data_card)
        exportDataCard = view.findViewById(R.id.export_data_card)
    }
    
    private fun setupListeners() {
        clearDataCard.setOnClickListener {
            showClearDataDialog()
        }
        
        exportDataCard.setOnClickListener {
            exportData()
        }
        
        view?.findViewById<MaterialCardView>(R.id.view_profile_card)?.setOnClickListener {
            mainActivity.navigateToFragment(ProfileFragment(), "Profile")
        }
        
        view?.findViewById<MaterialCardView>(R.id.reset_onboarding_card)?.setOnClickListener {
            showResetOnboardingDialog()
        }
    }
    
    private fun showClearDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to clear all data? This action cannot be undone.")
            .setPositiveButton("Clear") { _, _ ->
                mainActivity.getDataManager().clearAllData()
                Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun exportData() {
        try {
            val dataJson = mainActivity.getDataManager().exportData()
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, dataJson)
                type = "application/json"
            }
            
            startActivity(Intent.createChooser(shareIntent, "Export Data"))
            Toast.makeText(context, "Data exported", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showResetOnboardingDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Onboarding")
            .setMessage("This will show the onboarding screens again on next login. Continue?")
            .setPositiveButton("Reset") { _, _ ->
                authManager.resetOnboarding()
                Toast.makeText(context, "Onboarding reset. Logout and login again to see it.", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}





