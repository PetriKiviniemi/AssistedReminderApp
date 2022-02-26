    package com.example.assistedreminderapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assistedreminderapp.AssistedReminderApp
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.ui.theme.AssistedReminderAppTheme
import com.example.assistedreminderapp.util.createNotificationChannel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

    class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Maybe initialize there here instead of in Graph?

        if(!Graph.locationPermissionsEnabled)
        {
            if(!checkLocPermissions(this))
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        123
                    )
                    if(!checkLocPermissions(this))
                    {
                        Graph.locationPermissionsEnabled = true
                    }
                }
            }
            else
            {
                Graph.locationPermissionsEnabled = true
            }
        }
        else
        {
            //Quit the app
            return
        }

        createNotificationChannel(context = Graph.appContext)
        setContent {
            AssistedReminderAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AssistedReminderApp();
                }
            }
        }
    }

        private fun checkLocPermissions(context: Context): Boolean {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                return (
                        ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED)
                        &&
                        (
                                ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED)
            }
            return false
        }
    }