package com.example.assistedreminderapp

import android.app.Application
import com.example.assistedreminderapp.data.entity.User
import kotlinx.coroutines.coroutineScope

class AssistedReminderApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        Graph.provideContext(this)

    }
}