package com.example.assistedreminderapp.data.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "reminders",
    indices = [
        Index("id", unique=true),
        Index("reminder_user_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["reminder_user_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val reminderId: Long = 0,
    @ColumnInfo(name = "reminder_message") val reminderMessage: String,
    @ColumnInfo(name = "reminder_date") val reminderDate: Long,
    @ColumnInfo(name = "reminder_user_id") val reminder_user_id: Long,
)