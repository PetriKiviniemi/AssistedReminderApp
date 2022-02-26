package com.example.assistedreminderapp.ui.maps

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.R
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.room.RemindersFromUser
import com.example.assistedreminderapp.util.rememberMapViewWithLifeCycle
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@SuppressLint("MissingPermission")
@Composable
fun ReminderLocationMap(
    userId: Long?,
    navigateBack: (latlng: LatLng) -> Unit,
)
{

    val mapView = rememberMapViewWithLifeCycle()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    )
    {
        Button(
            onClick = {
                coroutineScope.launch {
                    val map = mapView.awaitMap()
                    showNearbyReminders(userId = userId, map = map)
                }
            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.wrapContentWidth(),
            enabled = true,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        )
        {
            Text(
                text = "Show nearby reminders",
                fontSize = 24.sp,
            )
        }
        AndroidView({mapView}) { mapView ->

            coroutineScope.launch {
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true
                if(Graph.locationPermissionsEnabled)
                {
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                } else {
                    map.isMyLocationEnabled = false
                    map.uiSettings.isMyLocationButtonEnabled = false
                }

                //TODO:: MODIFY THIS FUNCTION TO SET AND GET, NOW IT SETS THE CAMERA POS AND RETURNS IT
                getDeviceLocation(map)

                setMapLongClick(map = map, navigateBack = navigateBack)
            }
        }
    }
}

private fun showNearbyReminders(
    map: GoogleMap,
    userId: Long?,
    reminderRepository: ReminderRepository = Graph.reminderRepository
)
{
    if(userId == null)
        return

    runBlocking {
        launch {
            val remList: List<Reminder> = reminderRepository
                .getRemindersInRadius(userId = userId, radius = 100, currentPos = map.cameraPosition.target)
            remList.forEach {
                //ADD MARKER WITH REMINDER LOCATION
                val reminder = it
                val markerOptions = MarkerOptions()
                    .title("${reminder.reminder_message}")
                    .position(LatLng(reminder.reminder_loc_x, reminder.reminder_loc_y))
                map.addMarker(markerOptions)
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

private fun setMapLongClick(
    map: GoogleMap,
    navigateBack: (latlng: LatLng) -> Unit,
)
{
    map.setOnMapLongClickListener { latlng ->
        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.2f, Lng: %2$.2f",
            latlng.latitude,
            latlng.longitude
        )

        map.addMarker(
            MarkerOptions().position(latlng).title("Reminder location").snippet(snippet)
        ).apply {
            navigateBack(latlng)
        }
    }
}

//This example code is from developers.google.com android studio documentation
@SuppressLint("MissingPermission")
private fun getDeviceLocation(
    map: GoogleMap
): Location? {
    try {
        if (Graph.locationPermissionsEnabled) {
            val locationResult = Graph.fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    Graph.lastKnownLocation = task.result
                    if (Graph.lastKnownLocation != null) {
                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(Graph.lastKnownLocation!!.latitude,
                                Graph.lastKnownLocation!!.longitude), 15f))
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    val defaultLocation = LatLng(65.123989, 25.321107)
                    map?.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, 15f))
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        }
    } catch (e: SecurityException) {
        Log.e("Exception: %s", e.message, e)
    }

    return Graph.lastKnownLocation
}