package com.example.labexam3.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.data.Habit
import com.example.labexam3.data.Exercise
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for displaying progress analytics
 */
class ProgressFragment : Fragment() {
    
    private lateinit var mainActivity: MainActivity
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mainActivity = activity as MainActivity
        
        // Load data asynchronously to avoid blocking UI
        view.post {
            updateStatistics()
            setupCharts()
        }
    }
    
    
    private fun updateStatistics() {
        try {
            val habits = mainActivity.getDataManager().getHabits()
            val exercises = mainActivity.getDataManager().getExercises()
            
            // Update total habits count
            view?.findViewById<TextView>(R.id.total_habits_count)?.text = habits.size.toString()
            
            // Update completed today count
            val completedToday = habits.count { it.isCompleted }
            view?.findViewById<TextView>(R.id.completed_today_count)?.text = completedToday.toString()
            
            // Calculate weekly average
            val calendar = Calendar.getInstance()
            var totalCompletions = 0
            var totalDays = 0
            
            for (i in 0..6) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_MONTH, -i)
                
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                val dayCompletions = habits.count { habit ->
                    habit.completedDates.contains(dateStr)
                }
                
                if (habits.isNotEmpty()) {
                    totalCompletions += dayCompletions
                    totalDays++
                }
            }
            
            val weeklyAverage = if (totalDays > 0 && habits.isNotEmpty()) {
                ((totalCompletions.toFloat() / (totalDays * habits.size)) * 100).toInt()
            } else {
                0
            }
            
            view?.findViewById<TextView>(R.id.weekly_average_percent)?.text = "${weeklyAverage}%"
        } catch (e: Exception) {
            // Handle any errors gracefully
            view?.findViewById<TextView>(R.id.total_habits_count)?.text = "0"
            view?.findViewById<TextView>(R.id.completed_today_count)?.text = "0"
            view?.findViewById<TextView>(R.id.weekly_average_percent)?.text = "0%"
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh statistics and charts when fragment becomes visible
        refreshData()
    }
    
    private fun refreshData() {
        view?.post {
            updateStatistics()
            setupCharts()
        }
    }
    
    private fun setupCharts() {
        setupWeeklyChart()
        setupHydrationChart()
        setupPieChart()
    }
    
    private fun setupWeeklyChart() {
        val weeklyChart = view?.findViewById<BarChart>(R.id.weekly_chart) ?: return
        
        try {
            val habits = mainActivity.getDataManager().getHabits()
            if (habits.isEmpty()) {
                weeklyChart.setNoDataText("No habit data available")
                return
            }
            
            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()
            
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val fullDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            // Get last 7 days data
            for (i in 6 downTo 0) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_MONTH, -i)
                
                val dateStr = fullDateFormat.format(calendar.time)
                val dayLabel = dateFormat.format(calendar.time)
                
                val completedCount = habits.count { habit ->
                    habit.completedDates.contains(dateStr)
                }
                
                entries.add(BarEntry((6 - i).toFloat(), completedCount.toFloat()))
                labels.add(dayLabel)
            }
            
            val dataSet = BarDataSet(entries, "Completed Habits")
            dataSet.color = Color.parseColor("#4CAF50")
            dataSet.valueTextSize = 10f
            dataSet.valueTextColor = Color.BLACK
            
            val barData = BarData(dataSet)
            barData.barWidth = 0.8f
            
            weeklyChart.data = barData
            weeklyChart.description.isEnabled = false
            weeklyChart.setFitBars(true)
            weeklyChart.animateY(800)
            
            // X-axis configuration
            val xAxis = weeklyChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.textSize = 10f
            
            // Y-axis configuration
            weeklyChart.axisLeft.axisMinimum = 0f
            weeklyChart.axisLeft.textSize = 10f
            weeklyChart.axisRight.isEnabled = false
            
            weeklyChart.legend.isEnabled = false
            
            weeklyChart.invalidate()
        } catch (e: Exception) {
            android.util.Log.e("ProgressFragment", "Error setting up weekly chart: ${e.message}")
            weeklyChart.setNoDataText("Error loading chart data")
        }
    }
    
    private fun setupHydrationChart() {
        val hydrationChart = view?.findViewById<BarChart>(R.id.hydration_chart) ?: return
        
        try {
            val hydrationManager = com.example.labexam3.notification.HydrationReminderManager(requireContext())
            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()
            val calendar = Calendar.getInstance()
            
            // Get data for last 7 days
            for (i in 6 downTo 0) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_MONTH, -i)
                
                val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
                labels.add(dayName)
                
                // Only show today's actual data, past days are 0 for new users
                val glassCount = if (i == 0) {
                    hydrationManager.getHydrationData().glassesToday.toFloat()
                } else {
                    0f
                }
                
                entries.add(BarEntry((6 - i).toFloat(), glassCount))
            }
            
            val dataSet = BarDataSet(entries, "Glasses")
            dataSet.color = resources.getColor(R.color.accent_light_blue, null)
            dataSet.valueTextSize = 10f
            dataSet.valueTextColor = Color.BLACK
            
            val barData = BarData(dataSet)
            barData.barWidth = 0.6f
            
            hydrationChart.data = barData
            hydrationChart.description.isEnabled = false
            hydrationChart.setFitBars(true)
            hydrationChart.animateY(800)
            
            // X-axis configuration
            val xAxis = hydrationChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.textSize = 10f
            
            // Y-axis configuration
            hydrationChart.axisLeft.axisMinimum = 0f
            hydrationChart.axisLeft.textSize = 10f
            hydrationChart.axisLeft.granularity = 1f
            hydrationChart.axisRight.isEnabled = false
            
            hydrationChart.legend.isEnabled = false
            hydrationChart.invalidate()
        } catch (e: Exception) {
            android.util.Log.e("ProgressFragment", "Error setting up hydration chart: ${e.message}")
            hydrationChart.setNoDataText("Error loading hydration data")
        }
    }
    
    private fun setupPieChart() {
        val pieChart = view?.findViewById<PieChart>(R.id.habit_pie_chart) ?: return
        
        try {
            val exercises = mainActivity.getDataManager().getExercises()
            
            // Only count COMPLETED exercises
            val completedExercises = exercises.filter { it.isCompleted }
            
            if (completedExercises.isEmpty()) {
                pieChart.setNoDataText("No completed exercises yet")
                pieChart.invalidate()
                return
            }
            
            // Count completed exercises by type
            val typeCounts = mutableMapOf<String, Int>()
            completedExercises.forEach { exercise ->
                val typeName = exercise.type.name
                typeCounts[typeName] = (typeCounts[typeName] ?: 0) + 1
            }
            
            if (typeCounts.isEmpty()) {
                pieChart.setNoDataText("No completed exercises")
                pieChart.invalidate()
                return
            }
            
            // Create pie entries
            val entries = ArrayList<PieEntry>()
            typeCounts.forEach { (type, count) ->
                entries.add(PieEntry(count.toFloat(), type))
            }
            
            val dataSet = PieDataSet(entries, "")
            
            // Colors for different exercise types
            val colors = listOf(
                Color.parseColor("#FF6B6B"),  // Red - CARDIO
                Color.parseColor("#4ECDC4"),  // Teal - STRENGTH
                Color.parseColor("#95E1D3"),  // Light Green - FLEXIBILITY
                Color.parseColor("#9B59B6"),  // Purple - YOGA
                Color.parseColor("#F39C12"),  // Orange - SPORTS
                Color.parseColor("#E91E63")   // Pink - DANCE
            )
            dataSet.colors = colors
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = Color.WHITE
            dataSet.sliceSpace = 2f
            
            val pieData = PieData(dataSet)
            pieData.setValueFormatter(PercentFormatter(pieChart))
            
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.setUsePercentValues(true)
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setEntryLabelTextSize(10f)
            pieChart.centerText = "Exercise\nTypes"
            pieChart.setCenterTextSize(14f)
            pieChart.setCenterTextColor(Color.BLACK)
            pieChart.setDrawHoleEnabled(true)
            pieChart.setHoleColor(Color.WHITE)
            pieChart.setTransparentCircleRadius(55f)
            pieChart.animateY(800)
            
            pieChart.legend.isEnabled = true
            pieChart.legend.textSize = 11f
            pieChart.legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
            pieChart.legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            pieChart.legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
            pieChart.legend.setDrawInside(false)
            
            pieChart.invalidate()
        } catch (e: Exception) {
            android.util.Log.e("ProgressFragment", "Error setting up pie chart: ${e.message}")
            pieChart.setNoDataText("Error loading chart")
        }
    }
}
