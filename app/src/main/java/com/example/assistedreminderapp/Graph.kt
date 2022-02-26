package com.example.assistedreminderapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.repository.UserRepository
import com.example.assistedreminderapp.data.room.AssistedReminderAppDatabase
import com.example.assistedreminderapp.util.GeofenceReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

//Application wide dependency injection
object Graph {
    lateinit var database: AssistedReminderAppDatabase

    lateinit var appContext: Context
    var locationPermissionsEnabled: Boolean = false
    var lastKnownLocation: Location? = null
    lateinit var geofencingClient: GeofencingClient
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //TODO:: REQUEST PERMISSIONS FROM USER

    val userRepository by lazy {
        UserRepository(
            userDao = database.UserDao()
        )
    }

    val reminderRepository by lazy {
        ReminderRepository(
            reminderDao = database.ReminderDao()
        )
    }

    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(appContext, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun provideContext(context: Context)
    {
        appContext = context
        database = Room.databaseBuilder(context, AssistedReminderAppDatabase::class.java, "AssistedReminderApp.db")
            .fallbackToDestructiveMigration()
            .build()
        geofencingClient = LocationServices.getGeofencingClient(context)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }
}