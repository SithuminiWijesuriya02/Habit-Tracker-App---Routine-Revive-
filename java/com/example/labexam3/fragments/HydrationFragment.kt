package com.example.labexam3.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.notification.HydrationReminderManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * Fragment for managing hydration tracking and reminders
 */
class HydrationFragment : Fragment() {
    
    private lateinit var glassesTodayText: TextView
    private lateinit var progressText: TextView
    private lateinit var addGlassButton: MaterialButton
    private lateinit var removeGlassButton: MaterialButton
    private lateinit var settingsCard: MaterialCardView
    private lateinit var reminderSwitch: Switch
    private lateinit var intervalSeekBar: SeekBar
    private lateinit var intervalText: TextView
    private lateinit var goalEditText: EditText
    
    private lateinit var mainActivity: MainActivity
    private lateinit var hydrationManager: HydrationReminderManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mainActivity = activity as MainActivity
        hydrationManager = HydrationReminderManager(requireContext())
        initializeViews(view)
        setupListeners()
        loadHydrationData()
    }
    
    private fun initializeViews(view: View) {
        glassesTodayText = view.findViewById(R.id.glasses_today_text)
        progressText = view.findViewById(R.id.progress_text)
        addGlassButton = view.findViewById(R.id.add_glass_button)
        removeGlassButton = view.findViewById(R.id.remove_glass_button)
        settingsCard = view.findViewById(R.id.settings_card)
        reminderSwitch = view.findViewById(R.id.reminder_switch)
        intervalSeekBar = view.findViewById(R.id.interval_seek_bar)
        intervalText = view.findViewById(R.id.interval_text)
        goalEditText = view.findViewById(R.id.goal_edit_text)
    }
    
    private fun setupListeners() {
        addGlassButton.setOnClickListener {
            addGlass()
        }
        
        removeGlassButton.setOnClickListener {
            removeGlass()
        }
        
        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateReminderSettings()
        }
        
        intervalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val interval = progress + 15 // Minimum 15 minutes
                    intervalText.text = "$interval minutes"
                    updateReminderSettings()
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        settingsCard.setOnClickListener {
            showSettingsDialog()
        }
    }
    
    private fun loadHydrationData() {
        val hydrationData = hydrationManager.getHydrationData()
        
        glassesTodayText.text = hydrationData.glassesToday.toString()
        progressText.text = "Progress: ${String.format("%.0f", hydrationData.getProgressPercentage())}%"
        
        reminderSwitch.isChecked = hydrationData.reminderEnabled
        intervalSeekBar.progress = hydrationData.reminderIntervalMinutes - 15
        intervalText.text = "${hydrationData.reminderIntervalMinutes} minutes"
        goalEditText.setText(hydrationData.dailyGoal.toString())
        
        // Update button states
        removeGlassButton.isEnabled = hydrationData.glassesToday > 0
    }
    
    private fun addGlass() {
        val hydrationData = hydrationManager.getHydrationData()
        hydrationData.addGlass()
        mainActivity.getDataManager().updateHydrationData(hydrationData)
        loadHydrationData()
        
        if (hydrationData.isGoalReached()) {
            Toast.makeText(context, "🎉 Daily goal reached! Great job!", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun removeGlass() {
        val hydrationData = hydrationManager.getHydrationData()
        hydrationData.removeGlass()
        mainActivity.getDataManager().updateHydrationData(hydrationData)
        loadHydrationData()
    }
    
    private fun updateReminderSettings() {
        val interval = intervalSeekBar.progress + 15
        hydrationManager.updateReminderSettings(reminderSwitch.isChecked, interval)
    }
    
    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_hydration_settings, null)
        val goalEditText = dialogView.findViewById<EditText>(R.id.dialog_goal_edit_text)
        val hydrationData = hydrationManager.getHydrationData()
        
        goalEditText.setText(hydrationData.dailyGoal.toString())
        
        AlertDialog.Builder(requireContext())
            .setTitle("Hydration Settings")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newGoal = goalEditText.text.toString().toIntOrNull() ?: 8
                if (newGoal > 0) {
                    val updatedData = hydrationData.copy(dailyGoal = newGoal)
                    mainActivity.getDataManager().updateHydrationData(updatedData)
                    loadHydrationData()
                    Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter a valid goal", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}





