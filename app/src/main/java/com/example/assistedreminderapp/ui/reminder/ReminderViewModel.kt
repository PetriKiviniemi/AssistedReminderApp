package com.example.assistedreminderapp.ui.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.material.MaterialTheme
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
            setReminderNotification(reminder)
        }
    }

    init {
        createNotificationChannel(context = Graph.appContext)
        viewModelScope.launch {
            if(reminderId != null)
            {
                _reminder.value = Graph.reminderRepository.getReminder(reminderId)
                state.value = ReminderViewState(_reminder.value)
            }
        }
    }
}

private fun setReminderNotification(reminder: Reminder)
{
    //Only do this for reminders that have not been seen
    if(reminder.reminder_seen)
        return

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
                //We can now show the reminder in the list and mark it as "seen"
                //There is contradiction in the logic. Is notification seen when user taps it?
                //Or when the notification has been triggered?
                //Problem: What if user never taps the notification, we never show the reminder in the list?
                //This is why we mark it seen here.
                runBlocking {
                    launch {
                        val rem = Reminder(
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
                        reminderRepository.addReminder(rem)
                    }
                }
            }
        }

}

private fun createReminderNotification(
    reminder: Reminder,
)
{
    //Note:: This is a safe cast since there cannot possibly be enough reminders stored for int to overflow
    val notificationId = reminder.reminderId.toInt()
    //Custom layouts


    // Apply the layouts to the notification
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.notification_exclamation_mark)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setContentTitle("Hey! You have a new reminder:")
        .setContentText(reminder.reminder_message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(from(Graph.appContext))
    {
        //notificationId is unique for each notification
        if(reminder.notify_me)
            notify(notificationId, builder.build())
    }
}

private fun createNotificationChannel(context: Context)
{
    //Create notification channel for API version 26 and above
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelDescriptionText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }

        channel.setSound(
            Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://" + Graph.appContext.packageName + "/" + R.raw.guitar_notification_sound),
            Notification.AUDIO_ATTRIBUTES_DEFAULT
        )

        //register the channel
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

data class ReminderViewState(
    val reminder: Reminder? = null,
)