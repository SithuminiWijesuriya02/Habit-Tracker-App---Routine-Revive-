package com.example.labexam3.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.labexam3.LoginActivity
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.auth.AuthManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * Profile fragment showing user information and settings
 */
class ProfileFragment : Fragment() {
    
    private lateinit var authManager: AuthManager
    private lateinit var mainActivity: MainActivity
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mainActivity = activity as MainActivity
        authManager = AuthManager(requireContext())
        
        setupProfileInfo(view)
        setupButtons(view)
    }
    
    private fun setupProfileInfo(view: View) {
        val userEmail = authManager.getCurrentUserEmail()
        
        // Get username from SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name_$userEmail", null) 
            ?: userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        
        view.findViewById<TextView>(R.id.profile_name)?.text = userName
        view.findViewById<TextView>(R.id.profile_email)?.text = userEmail
    }
    
    private fun setupButtons(view: View) {
        // View Settings Card
        view.findViewById<MaterialCardView>(R.id.view_settings_card)?.setOnClickListener {
            mainActivity.navigateToFragment(SettingsFragment(), "Settings")
        }
        
        // Logout Card
        view.findViewById<MaterialCardView>(R.id.logout_card)?.setOnClickListener {
            showLogoutDialog()
        }
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                authManager.logout()
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                
                // Navigate to login screen
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
