package com.example.labexam3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.R
import com.example.labexam3.data.MoodEntry

/**
 * RecyclerView adapter for displaying mood journal entries
 */
class MoodEntriesAdapter(
    private val onMoodDelete: (MoodEntry) -> Unit,
    private val onMoodEdit: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodEntriesAdapter.MoodEntryViewHolder>() {
    
    private var moodEntries = mutableListOf<MoodEntry>()
    
    class MoodEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moodEmoji: TextView = itemView.findViewById(R.id.mood_emoji)
        val moodDescription: TextView = itemView.findViewById(R.id.mood_description)
        val moodDate: TextView = itemView.findViewById(R.id.mood_date)
        val moodTime: TextView = itemView.findViewById(R.id.mood_time)
        val moodNote: TextView = itemView.findViewById(R.id.mood_note)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_mood_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_mood_button)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_entry, parent, false)
        return MoodEntryViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        
        holder.moodEmoji.text = moodEntry.emoji
        holder.moodDescription.text = moodEntry.getMoodDescription()
        holder.moodDate.text = moodEntry.getFormattedDate()
        holder.moodTime.text = moodEntry.getFormattedTime()
        
        // Show note if available
        if (moodEntry.note.isNotEmpty()) {
            holder.moodNote.text = moodEntry.note
            holder.moodNote.visibility = View.VISIBLE
        } else {
            holder.moodNote.visibility = View.GONE
        }
        
        // Set up edit button listener
        holder.editButton.setOnClickListener {
            onMoodEdit(moodEntry)
        }
        
        // Set up delete button listener
        holder.deleteButton.setOnClickListener {
            onMoodDelete(moodEntry)
        }
    }
    
    override fun getItemCount(): Int = moodEntries.size
    
    fun updateMoodEntries(newMoodEntries: List<MoodEntry>) {
        moodEntries.clear()
        moodEntries.addAll(newMoodEntries)
        notifyDataSetChanged()
    }
}





