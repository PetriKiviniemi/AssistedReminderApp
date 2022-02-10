package com.example.assistedreminderapp.data.repository

import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.room.ReminderDao
import com.example.assistedreminderapp.data.room.RemindersFromUser
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val reminderDao: ReminderDao
){
    fun userReminders(userId: Long): Flow<List<RemindersFromUser>> {
        return reminderDao.remindersFromUser(userId)
    }

    suspend fun getReminder(reminderId: Long): Reminder? {
        return reminderDao.reminder(reminderId)
    }

    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)

    suspend fun deleteReminder(reminder: Reminder) = reminderDao.delete(reminder)
}