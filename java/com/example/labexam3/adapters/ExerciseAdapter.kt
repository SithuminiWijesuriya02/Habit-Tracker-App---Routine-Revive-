package com.example.labexam3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.R
import com.example.labexam3.data.Exercise

/**
 * RecyclerView adapter for displaying exercises
 */
class ExerciseAdapter(
    private val onExerciseToggle: (Exercise, Boolean) -> Unit,
    private val onExerciseEdit: (Exercise) -> Unit = {},
    private val onExerciseDelete: (Exercise) -> Unit = {}
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val exercises = mutableListOf<Exercise>()

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseCheckbox: CheckBox = itemView.findViewById(R.id.exercise_checkbox)
        val exerciseName: TextView = itemView.findViewById(R.id.exercise_name)
        val exerciseType: TextView = itemView.findViewById(R.id.exercise_type)
        val exerciseNotes: TextView = itemView.findViewById(R.id.exercise_notes)
        val exerciseIcon: ImageView = itemView.findViewById(R.id.exercise_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        holder.exerciseName.text = exercise.name
        holder.exerciseCheckbox.setOnCheckedChangeListener(null)
        holder.exerciseCheckbox.isChecked = exercise.isCompleted

        val typeDetails = "${exercise.getTypeDisplayName()} • ${exercise.getFormattedDuration()}"
        val caloriesText = if (exercise.caloriesBurned > 0) " • ${exercise.caloriesBurned} cal" else ""
        
        // Add completion date if exercise is completed
        val completionText = if (exercise.isCompleted && exercise.completedAt != null) {
            val dateFormat = java.text.SimpleDateFormat("MMM dd 'at' h:mm a", java.util.Locale.getDefault())
            "\n✓ Completed: ${dateFormat.format(exercise.completedAt)}"
        } else ""
        
        holder.exerciseType.text = typeDetails + caloriesText + completionText

        holder.exerciseIcon.setImageResource(exercise.getIconResource())

        if (exercise.notes.isNotEmpty()) {
            holder.exerciseNotes.text = exercise.notes
            holder.exerciseNotes.visibility = View.VISIBLE
        } else {
            holder.exerciseNotes.visibility = View.GONE
        }

        // Toggle handler (no data set reloads here)
        holder.exerciseCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (exercise.isCompleted != isChecked) {
                exercise.isCompleted = isChecked
                onExerciseToggle(exercise, isChecked)
                notifyItemChanged(holder.bindingAdapterPosition)
            }
        }

        val alpha = if (exercise.isCompleted) 0.6f else 1.0f
        holder.exerciseName.alpha = alpha
        holder.exerciseType.alpha = alpha
        holder.exerciseIcon.alpha = alpha

        holder.itemView.setOnLongClickListener {
            showContextMenu(holder.itemView, exercise)
            true
        }
    }

    override fun getItemCount(): Int = exercises.size

    fun updateExercises(newExercises: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newExercises)
        notifyDataSetChanged()
    }

    /** Expose current list so fragment can update progress without reloading. */
    fun getItems(): List<Exercise> = exercises.toList()

    private fun showContextMenu(view: View, exercise: Exercise) {
        val popup = androidx.appcompat.widget.PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.exercise_context_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> { onExerciseEdit(exercise); true }
                R.id.action_delete -> { onExerciseDelete(exercise); true }
                else -> false
            }
        }
        popup.show()
    }
}
