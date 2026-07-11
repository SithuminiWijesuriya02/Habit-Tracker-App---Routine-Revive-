package com.example.labexam3.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.adapters.HabitsAdapter
import com.example.labexam3.data.Habit
import com.example.labexam3.widget.HabitWidgetProvider
import java.util.UUID

/**
 * Fragment for managing daily habits
 */
class HabitsFragment : Fragment() {

    private lateinit var habitsRecyclerView: RecyclerView
    private lateinit var addHabitButton: com.google.android.material.button.MaterialButton
    private lateinit var progressText: TextView
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var progressPercentage: TextView
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        initializeViews(view)
        setupRecyclerView()
        setupFab()
        
        // Load habits asynchronously
        view.post {
            loadHabits()
        }
    }

    private fun initializeViews(view: View) {
        habitsRecyclerView = view.findViewById(R.id.habits_recycler_view)
        addHabitButton   = view.findViewById(R.id.add_habit_button)
        progressText     = view.findViewById(R.id.progress_text)
        progressBar      = view.findViewById(R.id.progress_bar)
        progressPercentage = view.findViewById(R.id.progress_percentage)
    }

    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            onHabitToggle = { habit ->
                toggleHabit(habit)   // NOTE: no toggling here, just persist & update UI
            },
            onHabitEdit = { habit ->
                showEditHabitDialog(habit)
            },
            onHabitDelete = { habit ->
                showDeleteHabitDialog(habit)
            }
        )

        habitsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }

    private fun setupFab() {
        addHabitButton.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun loadHabits() {
        try {
            val habits = mainActivity.getDataManager().getHabits()
            android.util.Log.d("HabitsFragment", "Loaded ${habits.size} habits")
            habits.forEachIndexed { index, habit ->
                android.util.Log.d("HabitsFragment", "Habit $index: ${habit.name}")
            }
            habitsAdapter.updateHabits(habits)
            updateProgress(habits)
            
            // Force RecyclerView to refresh
            habitsRecyclerView.post {
                habitsAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            android.util.Log.e("HabitsFragment", "Error loading habits: ${e.message}", e)
        }
    }

    private fun updateProgress(habits: List<Habit>) {
        val completedCount = habits.count { it.isCompleted }
        val totalCount = habits.size
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

        progressText.text = "$completedCount/$totalCount"
        // animate from current to new value (nicer), but simple set also fine
        progressBar.progress = percentage
        progressPercentage.text = "Progress: $percentage%"

        // If you want animation each time, uncomment:
        // val animator = android.animation.ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, percentage)
        // animator.duration = 400
        // animator.interpolator = android.view.animation.DecelerateInterpolator()
        // animator.start()
    }

    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_habit_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.edit_habit_description)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_habit))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()

                if (name.isNotEmpty()) {
                    val habit = Habit(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description
                    )
                    mainActivity.getDataManager().addHabit(habit)
                    loadHabits()
                    Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_habit_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.edit_habit_description)

        nameEditText.setText(habit.name)
        descriptionEditText.setText(habit.description)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.edit_habit))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()

                if (name.isNotEmpty()) {
                    val updatedHabit = habit.copy(name = name, description = description)
                    mainActivity.getDataManager().updateHabit(updatedHabit)
                    loadHabits()
                    Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showDeleteHabitDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_habit))
            .setMessage("Are you sure you want to delete \"${habit.name}\"?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                mainActivity.getDataManager().deleteHabit(habit.id)
                loadHabits()
                Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /** DO NOT toggle here — the adapter already changed the model.
     *  Just persist and refresh the progress UI.
     */
    private fun toggleHabit(habit: Habit) {
        mainActivity.getDataManager().updateHabit(habit)          // save new state
        updateProgress(habitsAdapter.getItems())                  // recalc from current list
        HabitWidgetProvider.updateAllWidgets(requireContext())    // keep your widget in sync
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadHabits()
    }
}
