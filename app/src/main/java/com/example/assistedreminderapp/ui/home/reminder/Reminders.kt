package com.example.assistedreminderapp.ui.home.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.room.RemindersFromUser
import com.example.assistedreminderapp.util.viewModelProviderFactoryOf
import com.google.accompanist.insets.systemBarsPadding
import java.util.*
import com.example.assistedreminderapp.util.toDateString

@Composable
fun Reminders(
    userId: Long?,
    modifier: Modifier,
    showReminderScreen: (userId: Long, reminderId: Long?) -> Unit,
)
{
    //NOTE:: This will never be the case
    if(userId == null)
        return

    //Creates the ViewModel with userid as argument. ViewModel can then retrieve reminders of user
    val viewModel: RemindersViewModel = viewModel(
        key = "reminder_list_$userId",
        factory = viewModelProviderFactoryOf { RemindersViewModel(userId = userId)}
    )
    val viewState by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize())
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        )
        {
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(
                text = "Reminders",
                color = Color.White,
                fontSize = 22.sp,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        ReminderList(
            list = viewState.reminders,
            showReminderScreen = showReminderScreen,
            viewModel = viewModel
        )
    }
}

@Composable
fun ReminderList(
    list: List<RemindersFromUser>,
    showReminderScreen: (userId: Long, reminderId: Long?) -> Unit,
    viewModel: RemindersViewModel,
)
{
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    )
    {
        items(list) { item -> if(item.reminder.reminder_seen)
            {
                ReminderListItem(
                    reminder = item.reminder,
                    onClick = {
                        showReminderScreen(item.reminder.creator_id, item.reminder.reminderId)
                    },
                    modifier = Modifier.fillParentMaxWidth(),
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ReminderListItem(
    reminder: Reminder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RemindersViewModel,
)
{
    val isOpen = remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = modifier.clickable { onClick() },
    ) {
        val (divider, reminderMsg, reminderDate, removeBtn) = createRefs()
        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        // Message
        Text(
            text = reminder.reminder_message,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(reminderMsg) {
                linkTo(
                    start = parent.start,
                    end = removeBtn.start,
                    startMargin = 24.dp,
                    endMargin = 10.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 2.dp)
                bottom.linkTo(parent.bottom, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = reminder.reminder_time.toDateString(),
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 14.sp,
            modifier = Modifier.constrainAs(reminderDate) {
                linkTo(
                    start = parent.start,
                    end = removeBtn.start,
                    startMargin = 8.dp,
                    endMargin = 15.dp,
                    bias = 0f
                )
                centerHorizontallyTo(parent)
                top.linkTo(reminderMsg.bottom, margin = 10.dp)
                bottom.linkTo(parent.bottom, margin = 20.dp)
            }
        )
        IconButton(
            modifier = Modifier
                .size(50.dp)
                .padding(6.dp)
                .constrainAs(removeBtn) {
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end)
                },
            onClick = { isOpen.value = true },
            ) {
            Icon(
                modifier = Modifier.size(34.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "",
                tint = MaterialTheme.colors.secondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        if(isOpen.value)
        {
            AlertDialog(
                onDismissRequest = {
                    isOpen.value = false
                },
                title = {
                    Text(
                        text = "Do you want to remove this reminder?",
                        fontSize = 24.sp
                    )
                },
                text = {
                    Text("")
                },
                confirmButton = {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.removeReminder(reminder)
                            isOpen.value = false
                                  },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary,
                        )
                    )
                    {
                        Text(
                            text = "Confirm",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isOpen.value = false},
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary,
                        )
                    )
                    {
                        Text(
                            text = "Cancel",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
            )
        }
    }
}