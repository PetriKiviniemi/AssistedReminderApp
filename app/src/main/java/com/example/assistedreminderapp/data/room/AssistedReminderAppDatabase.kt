package com.example.assistedreminderapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.entity.User

@Database(
    entities =[User::class, Reminder::class],
    version = 5,
    exportSchema = false,
)
abstract class AssistedReminderAppDatabase: RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun ReminderDao(): ReminderDao
}