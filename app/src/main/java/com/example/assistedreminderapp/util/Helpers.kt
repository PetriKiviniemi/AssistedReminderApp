package com.example.assistedreminderapp.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(this))
}

fun Date.toDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(this)
}