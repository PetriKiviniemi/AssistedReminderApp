package com.example.assistedreminderapp.data.room

import androidx.room.*
import com.example.assistedreminderapp.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao{

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN user_table ON reminders.creator_id= user_table.id
        WHERE creator_id = :userId
    """)
    abstract fun remindersFromUser(userId: Long): Flow<List<RemindersFromUser>>

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN user_table ON reminders.creator_id= user_table.id
        WHERE creator_id = :userId
    """)
    abstract suspend fun remindersFromUserAsList(userId: Long): List<RemindersFromUser>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    abstract suspend fun reminder(reminderId: Long): Reminder?

    @Query("SELECT * FROM reminders ORDER BY id DESC LIMIT 0, 1")
    abstract suspend fun getLatestReminder(): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Reminder): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Reminder)

    @Delete
    abstract suspend fun delete(entity: Reminder): Int
}