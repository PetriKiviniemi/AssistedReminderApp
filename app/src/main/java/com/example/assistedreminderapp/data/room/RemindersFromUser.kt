package com.example.assistedreminderapp.data.room

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.entity.User
import java.util.*

class RemindersFromUser(){
    @Embedded
    lateinit var reminder: Reminder

    @Relation(parentColumn = "creator_id", entityColumn = "id")
    lateinit var _users: List<User>

    @get:Ignore
    val user: User
        get() = _users[0]

    operator fun component1() = reminder
    operator fun component2() = user

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is RemindersFromUser -> reminder == other.reminder && _users == other._users
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(reminder, _users)
}
