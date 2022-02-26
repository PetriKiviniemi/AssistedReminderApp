package com.example.assistedreminderapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assistedreminderapp.data.entity.User
import kotlinx.coroutines.coroutineScope

class AssistedReminderApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        Graph.provideContext(this)
    }
}