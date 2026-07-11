# Routine Revive - Personal Wellness Tracker App

## Overview

Routine Revive is an Android-based personal wellness tracking application developed using **Android Studio and Kotlin**. The app is designed to help users build and maintain healthy daily routines by tracking habits, exercise activities, hydration levels, and mood entries through a simple and organized interface.

The application provides a complete wellness management experience with features such as habit tracking, exercise management, hydration reminders, mood journaling, progress visualization, and personalized statistics. It combines user-friendly UI design with efficient local data management to provide a smooth and reliable experience.

## Features

### User Management

* Splash screen and onboarding experience
* User login and sign-up functionality
* Personalized dashboard with easy navigation

### Habit & Exercise Tracking

* Create, edit, and delete daily habits and exercise tasks
* Display activities using RecyclerView cards
* Track completion status using checkboxes
* Manage routine progress efficiently

### Hydration Tracking

* Set daily water intake goals
* Monitor hydration progress
* Receive customized hydration reminders using AlarmManager

### Mood Journal

* Record daily moods using emojis and short notes
* Track mood history
* Visualize weekly mood trends using charts

### Progress & Analytics

* Display personalized wellness statistics
* Visualize progress using bar charts and pie charts
* Monitor weekly performance and routine improvements

### Additional Features

* Home screen widget for quick habit progress viewing
* BootReceiver to maintain reminders after device restart
* Local data storage for maintaining user preferences and records

## Database & Data Management

The application uses **Room Database** for local data persistence. The database architecture follows a structured approach using:

* **Entities** to represent application data
* **DAO classes** for database operations:

  * HabitDao
  * ExerciseDao
  * HydrationDao
  * MoodEntryDao
* **WellnessDatabase** as the main database instance

The app implements complete **CRUD operations**, allowing users to:

* Create new records
* View saved information
* Update existing data
* Delete unwanted entries

## Technologies Used

* Kotlin
* Android Studio
* Room Database
* SQLite
* RecyclerView
* Fragments
* SharedPreferences
* AlarmManager
* MPAndroidChart
* Material Design Components

## Project Objectives

* Develop a mobile application to support healthy lifestyle management
* Apply Android development concepts using Kotlin
* Implement local database management using Room ORM
* Practice CRUD operations and structured data handling
* Create an interactive and user-friendly mobile experience
