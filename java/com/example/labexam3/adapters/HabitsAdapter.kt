package com.example.labexam3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.R
import com.example.labexam3.data.Habit

/**
 * RecyclerView adapter for displaying habits
 */
class HabitsAdapter(
    private val onHabitToggle: (Habit) -> Unit,
    private val onHabitEdit: (Habit) -> Unit = {},
    private val onHabitDelete: (Habit) -> Unit = {}
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    private val habits = mutableListOf<Habit>()

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitCheckbox: CheckBox = itemView.findViewById(R.id.habit_checkbox)
        val habitName: TextView = itemView.findViewById(R.id.habit_name)
        val habitDescription: TextView = itemView.findViewById(R.id.habit_description)
        val habitIcon: ImageView = itemView.findViewById(R.id.habit_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name
        holder.habitDescription.text = habit.description

        // Clear old listener, set state, then set listener
        holder.habitCheckbox.setOnCheckedChangeListener(null)
        holder.habitCheckbox.isChecked = habit.isCompleted
        holder.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) habit.markCompleted() else habit.markNotCompleted()
            onHabitToggle(habit)            // tell the fragment to persist & update progress
            notifyItemChanged(position)     // refresh visuals
        }

        // Visual dim if completed
        val alpha = if (habit.isCompleted) 0.6f else 1.0f
        holder.habitName.alpha = alpha
        holder.habitDescription.alpha = alpha
        holder.habitIcon.alpha = alpha

        // Icon by name
        holder.habitIcon.setImageResource(getHabitIcon(habit.name))

        // Long-press context menu
        holder.itemView.setOnLongClickListener {
            showContextMenu(holder.itemView, habit)
            true
        }
    }

    private fun getHabitIcon(habitName: String): Int {
        return when (habitName.lowercase()) {
            "drink water", "hydration", "water" -> R.drawable.ic_water
            "meditate", "meditation" -> R.drawable.ic_meditation
            "exercise", "workout", "gym" -> R.drawable.ic_exercise
            "read", "reading" -> R.drawable.ic_reading
            "sleep", "rest" -> R.drawable.ic_sleep
            "walk", "steps" -> R.drawable.ic_walk
            else -> R.drawable.ic_habits
        }
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }

    /** Expose current list so the fragment can recalc progress without reloading. */
    fun getItems(): List<Habit> = habits.toList()

    private fun showContextMenu(view: View, habit: Habit) {
        val popup = androidx.appcompat.widget.PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.habit_context_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> { onHabitEdit(habit); true }
                R.id.action_delete -> { onHabitDelete(habit); true }
                else -> false
            }
        }
        popup.show()
    }
}
