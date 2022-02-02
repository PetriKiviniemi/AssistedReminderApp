package com.example.assistedreminderapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_table",
    indices = [
        Index("id", unique = true)
    ]
)
data class User(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
   @ColumnInfo(name = "username") val username: String,
   @ColumnInfo(name = "password") val password: String,
)