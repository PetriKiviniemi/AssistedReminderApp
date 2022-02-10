package com.example.assistedreminderapp.data.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "reminders",
    indices = [
        Index("id", unique=true),
        Index("creator_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creator_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val reminderId: Long = 0,
    @ColumnInfo(name = "message") val reminder_message: String,
    @ColumnInfo(name = "location_x") val reminder_loc_x: Double,
    @ColumnInfo(name = "location_y") val reminder_loc_y: Double,
    @ColumnInfo(name = "reminder_time") val reminder_time: Long,
    @ColumnInfo(name = "creation_time") val creation_time: Long,
    @ColumnInfo(name = "creator_id") val creator_id: Long,
    @ColumnInfo(name = "reminder_seen") val reminder_seen: Boolean,
)