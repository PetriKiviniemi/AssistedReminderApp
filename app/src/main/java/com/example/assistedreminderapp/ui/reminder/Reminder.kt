package com.example.assistedreminderapp.ui.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistedreminderapp.R
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.ui.home.reminder.RemindersViewModel
import com.example.assistedreminderapp.util.*
import com.google.accompanist.insets.systemBarsPadding
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun Reminder(
    userId: Long,
    reminderId: Long? = null,
    showHomeScreen: (userId: Long) -> Unit,
)
{
    val viewModel: ReminderViewModel = viewModel(
        key = "reminder_$userId",
        factory = viewModelProviderFactoryOf { ReminderViewModel(userId = userId, reminderId = reminderId) }
    )
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    //Reminder fields in states
    val message = rememberSaveable { mutableStateOf(" ")}
    val loc_x = rememberSaveable { mutableStateOf(0.toDouble())}
    val loc_y = rememberSaveable { mutableStateOf(0.toDouble())}
    val reminder_seen = rememberSaveable { mutableStateOf(false)}
    val date = rememberSaveable { mutableStateOf(Calendar.getInstance().getTime().toDateString())}
    val dateAsLong = rememberSaveable { mutableStateOf(0L)}

    if(viewState.reminder != null)
    {
        message.value = viewState.reminder!!.reminder_message
        loc_x.value = viewState.reminder!!.reminder_loc_x
        loc_y.value = viewState.reminder!!.reminder_loc_y
        reminder_seen.value = viewState.reminder!!.reminder_seen
        date.value = viewState.reminder!!.reminder_time.toDateString()
        dateAsLong.value = viewState.reminder!!.reminder_time
    }

    val mYear = rememberSaveable{ mutableStateOf(2022)}
    val mMonth = rememberSaveable{ mutableStateOf(1)}
    val mDay = rememberSaveable{ mutableStateOf(1)}
    val mHour = rememberSaveable{ mutableStateOf(12)}
    val mMinute = rememberSaveable{ mutableStateOf(0)}

    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        {
            _:TimePicker, hour: Int, minute: Int ->
                val cal = Calendar.getInstance()
                cal.set(mYear.value, mMonth.value, mDay.value, hour, minute)

                //Since Calendar is singleton, we have to do this in 2 parts for it to render properly
                date.value = cal.getTime().toDateString()
                dateAsLong.value = cal.timeInMillis
        }, mHour.value, mMinute.value, false
    )

    val datePickerDialog = DatePickerDialog(
       LocalContext.current,
        {
            _:DatePicker, year: Int, month: Int, day: Int ->
                val cal = Calendar.getInstance()
                cal.set(year,month,day,mHour.value,mMinute.value)

                date.value = cal.getTime().toDateString()
                dateAsLong.value = cal.timeInMillis
                mYear.value = year
                mMonth.value = month
                mDay.value = day

                timePickerDialog.show()
        }, mYear.value, mMonth.value, mDay.value
    )


    Surface()
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
        {
            ReminderNavBar(
                backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.85f),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(24.dp)
            )
            {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Reminder",
                    color = Color.White,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    label = { Text(text = "Reminder message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {/* TODO:: IMPLEMENT */},
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Text(
                        text = "Select location",
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        datePickerDialog.show()
                    },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                {
                    Text(
                        text = date.value,
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            //Update or insert reminder
                            if(viewState.reminder != null)
                            {
                                viewModel.addReminder(
                                    Reminder(
                                        reminder_message = message.value,
                                        reminder_loc_x = loc_x.value,
                                        reminder_loc_y = loc_y.value,
                                        reminder_time = dateAsLong.value,
                                        creation_time = System.currentTimeMillis(),
                                        creator_id = viewState.reminder!!.creator_id,
                                        reminder_seen = viewState.reminder!!.reminder_seen,
                                        reminderId = viewState.reminder!!.reminderId
                                    )
                                )
                            }
                            else
                            {
                                viewModel.addReminder(
                                    Reminder(
                                        reminder_message = message.value,
                                        reminder_loc_x = loc_x.value,
                                        reminder_loc_y = loc_y.value,
                                        reminder_time = dateAsLong.value,
                                        creation_time = System.currentTimeMillis(),
                                        creator_id = userId,
                                        reminder_seen = false,
                                    )
                                )
                            }
                            showHomeScreen(userId)
                        }
                    },
                    enabled = true,
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                    )

                ) {
                    Text(
                        text = "Save",
                        fontSize = 24.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderNavBar(
    backgroundColor: Color,
)
{
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        backgroundColor = backgroundColor,
        actions = {}
    )
}