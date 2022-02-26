package com.example.assistedreminderapp.ui.reminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.material.MaterialTheme
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.Graph.reminderRepository
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.repository.UserRepository
import com.example.assistedreminderapp.util.NotificationWorker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import com.example.assistedreminderapp.R
import com.example.assistedreminderapp.ui.MainActivity
import com.example.assistedreminderapp.util.GeofenceReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.example.assistedreminderapp.util.*
import java.util.*

class ReminderViewModel(
    userId: Long,
    reminderId: Long? = null,
) : ViewModel()
{
    private val _state = MutableStateFlow(ReminderViewState())
    private val _reminder = MutableStateFlow<Reminder?>(null)

    val state: MutableStateFlow<ReminderViewState>
        get() = _state

    suspend fun addReminder(reminder: Reminder)
    {
        viewModelScope.launch {
            reminderRepository.addReminder(reminder)
            //Check if the reminder was new reminder, or whether it was modification of an existing one
            //This is necessary for geofence broadcast receiver's parameters, since we want to create
            //The notification there, but we need the reminder's real id
            if(reminder.reminderId == 0L)
            {
                val rem = reminderRepository.getLatestReminder()
                if(rem != null)
                    setReminderNotification(rem)
            }
            else
                setReminderNotification(reminder)
        }
    }

    init {
        viewModelScope.launch {
            if(reminderId != null)
            {
                _reminder.value = Graph.reminderRepository.getReminder(reminderId)
                state.value = ReminderViewState(_reminder.value)
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun setReminderNotification(
    reminder: Reminder,
    reminderRepository: ReminderRepository = Graph.reminderRepository
)
{
    //Only do this for reminders that have not been seen
    if(reminder.reminder_seen)
        return

    //Check if both loc and time are null,
    //then there are no triggers for notification and it can be null
    var isLocSet: Boolean = false
    var isTimeSet: Boolean = false

    //Since reminder time and location are both optional, check both
    if(reminder.reminder_loc_x != 0.0)
    {
        val geofence = Geofence.Builder()
            .setRequestId(reminder.reminderId.toString())
            .setCircularRegion(
                reminder.reminder_loc_x,
                reminder.reminder_loc_y,
                20.0f
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(2)
            .build()

        val geofencingRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

        Graph.geofencingClient.addGeofences(geofencingRequest, Graph.geofencePendingIntent).run {
            addOnFailureListener{
                Log.d("GEOFENCE_EXCEPTION", it.toString())
            }
            addOnSuccessListener {
                //Geofence added succesfully!
                Log.d(
                    "GEOFENCE_SUCCESSFUL",
                    "Geofence added succesfully. Id: " + geofence.requestId + "\n" +
                            " latlng: " + reminder.reminder_loc_x + " : " + reminder.reminder_loc_y
                )
            }
        }
        isLocSet = true
    }

    //Check if time is null
    val cal = Calendar.getInstance()
    cal.set(2022, 1, 1, 12, 0)
    val calLong = cal.timeInMillis

    //If user has not set the time value, don't set timed notification
    if(reminder.reminder_time != calLong)
    {
        val workManager = WorkManager.getInstance(Graph.appContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val curTime = System.currentTimeMillis()
        val timeDelta = reminder.reminder_time - curTime
        if(timeDelta < 0)
            return

        val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(timeDelta, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()


        workManager.enqueue(notificationWorker)

        //Monitor state of work (by notificationWorker)
        workManager.getWorkInfoByIdLiveData(notificationWorker.id)
            .observeForever { workInfo ->
                if(workInfo.state == WorkInfo.State.SUCCEEDED) {
                    createReminderNotification(reminder)
                }
            }
        isTimeSet = true
    }

    if(!isLocSet && !isTimeSet)
    {
        runBlocking {
            launch {
                //Mark the reminder as seen
                val newRem = Reminder(
                    reminderId = reminder.reminderId,
                    reminder_message = reminder.reminder_message,
                    reminder_time = reminder.reminder_time,
                    reminder_loc_x = reminder.reminder_loc_x,
                    reminder_loc_y = reminder.reminder_loc_y,
                    reminder_seen = true,
                    creation_time = reminder.creation_time,
                    creator_id = reminder.creator_id,
                    notify_me = reminder.notify_me
                )
                Graph.reminderRepository.addReminder(newRem)
            }
        }
    }

}


data class ReminderViewState(
    val reminder: Reminder? = null,
)