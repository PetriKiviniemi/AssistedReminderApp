package com.example.assistedreminderapp.data.repository

import android.location.Location
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.room.ReminderDao
import com.example.assistedreminderapp.data.room.RemindersFromUser
import com.example.assistedreminderapp.util.distanceInMeters
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val reminderDao: ReminderDao
){
    fun userReminders(userId: Long): Flow<List<RemindersFromUser>> {
        return reminderDao.remindersFromUser(userId)
    }

    suspend fun userRemindersAsList(userId: Long): List<RemindersFromUser> {
        return reminderDao.remindersFromUserAsList(userId)
    }

    suspend fun getReminder(reminderId: Long): Reminder? {
        return reminderDao.reminder(reminderId)
    }

    //Radius in meters
    suspend fun getRemindersInRadius(userId: Long, currentPos: LatLng, radius: Long): List<Reminder> {
        val finalList = mutableListOf<Reminder>()
        val userRems = userRemindersAsList(userId)
        userRems.forEach { item ->
            val reminder = item.reminder
            //Way of calculating new LatLng given a radius. Could be used to draw a circle on the map
            /*
            val newLatitude = reminder.reminder_loc_x + ((radius * 0.001) / 6378) * (180 / Math.PI)
            val newLongitude = reminder.reminder_loc_y +
                    ((radius * 0.001) / 6378) *
                    (180 / Math.PI) / Math.cos(reminder.reminder_loc_x * (Math.PI / 180))

            */

            val remLatLng = LatLng(reminder.reminder_loc_x, reminder.reminder_loc_y)
            var distInMeters = distanceInMeters(currentPos, remLatLng)
            //If in 1km distance
            if(distInMeters < radius)
                finalList.add(reminder)
        }
        return finalList.toList()
    }

    suspend fun getLatestReminder(): Reminder? {
        return reminderDao.getLatestReminder()
    }

    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)

    suspend fun deleteReminder(reminder: Reminder) = reminderDao.delete(reminder)
}