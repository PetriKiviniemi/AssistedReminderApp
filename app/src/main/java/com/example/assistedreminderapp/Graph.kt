package com.example.assistedreminderapp

import android.content.Context
import androidx.room.Room
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.repository.UserRepository
import com.example.assistedreminderapp.data.room.AssistedReminderAppDatabase

//Application wide dependency injection
object Graph {
    lateinit var database: AssistedReminderAppDatabase

    val userRepository by lazy {
        UserRepository(
            userDao = database.UserDao()
        )
    }

    val reminderRepository by lazy {
        ReminderRepository(
            reminderDao = database.ReminderDao()
        )
    }

    val loggedInUser: Long? = null

    fun provideContext(context: Context)
    {
        database = Room.databaseBuilder(context, AssistedReminderAppDatabase::class.java, "AssistedReminderApp.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}