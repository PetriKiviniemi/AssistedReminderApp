package com.example.assistedreminderapp.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.R
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(this))
}

fun Date.toDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(this)
}

fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}

fun distanceInMeters(pos1: LatLng, pos2: LatLng): Double {
    val t = pos1.longitude - pos2.longitude
    var d =
        Math.sin(deg2rad(pos1.latitude)) * Math.sin(deg2rad(pos2.latitude))
        + Math.cos(deg2rad(pos1.latitude)) * Math.cos(deg2rad(pos2.latitude)) * Math.cos(deg2rad(t))
    d = Math.acos(d)
    d = rad2deg(d)
    d *= 60 * 1.1515
    d *= 1.609344
    d *= 0.001
    return d

}

//Nofications
fun createReminderNotification(
    reminder: Reminder,
    reminderRepository: ReminderRepository = Graph.reminderRepository
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

    with(NotificationManagerCompat.from(Graph.appContext))
    {
        //notificationId is unique for each notification
        if(reminder.notify_me)
            notify(notificationId, builder.build())
    }

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
            Graph.reminderRepository.addReminder(rem)
        }
    }
}

fun createNotificationChannel(context: Context)
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


