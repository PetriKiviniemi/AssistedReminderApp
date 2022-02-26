package com.example.assistedreminderapp.ui

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Home: Screen("home/{userId}"){
        fun createRoute(userId: Long) = "home/$userId"
    }
    object Profile: Screen("profile/{userId}") {
        fun createRoute(userId: Long) = "profile/$userId"
    }
    object Reminder: Screen("reminder/userId={userId}?reminderId={reminderId}"){
        fun createRoute(userId: Long, reminderId: Long?) = "reminder/userId=$userId?reminderId=$reminderId"
    }
    object ReminderLocationMap: Screen( "reminder/reminderLocationMap?userId={userId}")
    {
        fun createRoute(userId: Long) = "reminder/reminderLocationMap?userId=$userId"
    }
    fun defaultRoute() = route
}