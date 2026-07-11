package com.example.labexam3.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.labexam3.MainActivity
import com.example.labexam3.R
import com.example.labexam3.data.DataManager

/**
 * Home screen widget showing today's habit completion percentage
 * This provides users with quick access to their daily progress
 */
class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is removed
        super.onDisabled(context)
    }

    companion object {
        /**
         * Update a single widget instance with current habit data
         */
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Get habit completion data
            val dataManager = DataManager(context)
            val habits = dataManager.getHabits()
            val completedCount = habits.count { it.isCompleted }
            val totalCount = habits.size
            val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0

            // Create RemoteViews for the widget layout
            val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
            
            // Update widget content
            views.setTextViewText(R.id.widget_progress_text, "$completedCount/$totalCount")
            views.setTextViewText(R.id.widget_percentage_text, "$percentage%")
            views.setProgressBar(R.id.widget_progress_bar, 100, percentage, false)
            
            // Set up click intent to open the app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /**
         * Update all widget instances - call this when habit data changes
         */
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, HabitWidgetProvider::class.java)
            )
            
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
