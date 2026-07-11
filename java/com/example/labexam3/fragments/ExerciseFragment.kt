package com.example.labexam3.fragments

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.adapters.ExerciseAdapter
import com.example.labexam3.data.Exercise
import com.example.labexam3.data.ExerciseType
import com.google.android.material.button.MaterialButton
import java.util.UUID

/**
 * Fragment for managing exercise routines
 */
class ExerciseFragment : Fragment() {

    private lateinit var exerciseRecyclerView: RecyclerView
    private lateinit var addExerciseButton: MaterialButton
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var mainActivity: MainActivity

    // Progress views
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressPercentage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity
        initializeViews(view)
        setupRecyclerView()
        setupButton()
        loadExercises()
    }

    private fun initializeViews(view: View) {
        exerciseRecyclerView = view.findViewById(R.id.exercise_recycler_view)
        addExerciseButton = view.findViewById(R.id.add_exercise_button)
        progressText = view.findViewById(R.id.progress_text)
        progressBar = view.findViewById(R.id.progress_bar)
        progressPercentage = view.findViewById(R.id.progress_percentage)
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(
            onExerciseToggle = { exercise, isChecked ->
                toggleExercise(exercise, isChecked)   // persist + update header only
            },
            onExerciseEdit = { exercise ->
                showEditExerciseDialog(exercise)
            },
            onExerciseDelete = { exercise ->
                showDeleteExerciseDialog(exercise)
            }
        )
        exerciseRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseAdapter
        }
    }

    private fun setupButton() {
        addExerciseButton.setOnClickListener { showAddExerciseDialog() }
    }

    private fun loadExercises() {
        try {
            val exercises = mainActivity.getDataManager().getExercises()
            android.util.Log.d("ExerciseFragment", "Loaded ${exercises.size} exercises")
            exercises.forEachIndexed { index, exercise ->
                android.util.Log.d("ExerciseFragment", "Exercise $index: ID=${exercise.id}, Name=${exercise.name}")
            }
            exerciseAdapter.updateExercises(exercises)
            updateProgress(exercises)
            
            // Force RecyclerView to refresh
            exerciseRecyclerView.post {
                exerciseAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            android.util.Log.e("ExerciseFragment", "Error loading exercises: ${e.message}", e)
        }
    }

    // Smooth animated progress update (match Habits)
    private fun updateProgress(exercises: List<Exercise>) {
        val completedCount = exercises.count { it.isCompleted }
        val totalCount = exercises.size
        val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

        progressText.text = "$completedCount/$totalCount"
        progressPercentage.text = "Progress: $percentage%"

        val current = progressBar.progress
        val animator = ObjectAnimator.ofInt(progressBar, "progress", current, percentage)
        animator.duration = 800
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    private fun showAddExerciseDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_exercise, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_name)
        val durationEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_duration)
        val caloriesEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_calories)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.exercise_type_spinner)
        val notesEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_notes)

        val exerciseTypes = ExerciseType.values().map { it.name }
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exerciseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Add Exercise")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val duration = durationEditText.text.toString().toIntOrNull() ?: 0
                val calories = caloriesEditText.text.toString().toIntOrNull() ?: 0
                val type = ExerciseType.valueOf(exerciseTypes[typeSpinner.selectedItemPosition])
                val notes = notesEditText.text.toString().trim()

                if (name.isNotEmpty() && duration > 0) {
                    val exercise = Exercise(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        type = type,
                        duration = duration,
                        caloriesBurned = calories,
                        notes = notes
                    )
                    android.util.Log.d("ExerciseFragment", "Adding exercise: $name")
                    mainActivity.getDataManager().addExercise(exercise)
                    
                    // Wait a moment for database to save, then reload
                    view?.postDelayed({
                        loadExercises()
                    }, 100)
                    
                    Toast.makeText(context, "Exercise added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter exercise name and duration", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditExerciseDialog(exercise: Exercise) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_exercise, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_name)
        val durationEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_duration)
        val caloriesEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_calories)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.exercise_type_spinner)
        val notesEditText = dialogView.findViewById<EditText>(R.id.edit_exercise_notes)

        nameEditText.setText(exercise.name)
        durationEditText.setText(exercise.duration.toString())
        caloriesEditText.setText(exercise.caloriesBurned.toString())
        notesEditText.setText(exercise.notes)

        val exerciseTypes = ExerciseType.values().map { it.name }
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exerciseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter
        typeSpinner.setSelection(exercise.type.ordinal)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Exercise")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val duration = durationEditText.text.toString().toIntOrNull() ?: 0
                val calories = caloriesEditText.text.toString().toIntOrNull() ?: 0
                val type = ExerciseType.valueOf(exerciseTypes[typeSpinner.selectedItemPosition])
                val notes = notesEditText.text.toString().trim()

                if (name.isNotEmpty() && duration > 0) {
                    val updatedExercise = exercise.copy(
                        name = name,
                        type = type,
                        duration = duration,
                        caloriesBurned = calories,
                        notes = notes
                    )
                    mainActivity.getDataManager().updateExercise(updatedExercise)
                    
                    // Wait a moment for database to save, then reload
                    view?.postDelayed({
                        loadExercises()
                    }, 100)
                    
                    Toast.makeText(context, "Exercise updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please enter exercise name and duration", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteExerciseDialog(exercise: Exercise) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Exercise")
            .setMessage("Are you sure you want to delete \"${exercise.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                mainActivity.getDataManager().deleteExercise(exercise.id)
                
                // Wait a moment for database to delete, then reload
                view?.postDelayed({
                    loadExercises()
                }, 100)
                
                Toast.makeText(context, "Exercise deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Persist new state and update header only.
     * IMPORTANT: Do NOT reload the whole list here (avoids RecyclerView layout crashes).
     */
    private fun toggleExercise(exercise: Exercise, isChecked: Boolean) {
        // Update completion status and set completion date
        val updatedExercise = exercise.copy(
            isCompleted = isChecked,
            completedAt = if (isChecked) java.util.Date() else null
        )
        mainActivity.getDataManager().updateExercise(updatedExercise)

        // Update header from the adapter's current items (no dataset reset)
        updateProgress((exerciseRecyclerView.adapter as ExerciseAdapter).getItems())
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadExercises()
    }
}
