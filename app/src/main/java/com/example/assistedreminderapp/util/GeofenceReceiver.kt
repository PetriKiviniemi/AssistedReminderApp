package com.example.assistedreminderapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.Graph.reminderRepository
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GeofenceReceiver(
    reminderRepository: ReminderRepository = Graph.reminderRepository
): BroadcastReceiver() {
    //TODO:: Intent is nullable. Handle it
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null) {
            if(intent == null)
            {
                Log.d("NULL_INTENT", "Intent was null")
                return
            }

            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if(geofencingEvent.hasError()) {
                //TODO:: Create error message. Only useful for debugging
            } else {
                var geofenceEventIdList = mutableListOf<String>()
                geofencingEvent.triggeringGeofences.forEach {
                    val geofence = it.requestId
                    geofenceEventIdList.add(geofence)
                    runBlocking {
                        launch {
                            val reqId = it.requestId.toLong()
                            if(reqId >= 0)
                            {
                                val reminder = reminderRepository.getReminder(reqId)
                                if(reminder != null)
                                {
                                    createReminderNotification(reminder)
                                }
                            }
                        }
                    }
                }
                Graph.geofencingClient.removeGeofences(geofenceEventIdList)
            }
        }
    }
}