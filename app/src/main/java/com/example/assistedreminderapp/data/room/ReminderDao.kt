package com.example.assistedreminderapp.data.room

import androidx.room.*
import com.example.assistedreminderapp.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao{

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN user_table ON reminders.reminder_user_id = user_table.id
        WHERE reminder_user_id = :userId
    """)
    abstract fun remindersFromUser(userId: Long): Flow<List<RemindersFromUser>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    abstract fun reminder(reminderId: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Reminder): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Reminder)

    @Delete
    abstract suspend fun delete(entity: Reminder): Int
}