package com.example.assistedreminderapp.ui.home.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.room.RemindersFromUser
import com.example.assistedreminderapp.util.viewModelProviderFactoryOf
import com.google.accompanist.insets.systemBarsPadding
import org.intellij.lang.annotations.JdkConstants
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Reminders(
    userId: Long?,
    modifier: Modifier
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
            list = viewState.reminders
        )
    }
}

//TODO:: Change to user RemindersFromUser and fetch from database
@Composable
fun ReminderList(list: List<Reminder>)
{
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    )
    {
        items(list) { item ->
            ReminderListItem(
                reminder = item,
                onClick = {},
                modifier = Modifier.fillParentMaxWidth(),
            )
        }
    }
}

@Composable
fun ReminderListItem(
    reminder: Reminder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
{
    ConstraintLayout(
        modifier = modifier.clickable { onClick() },
    ) {

        val (divider, reminderMsg, reminderDate) = createRefs()
        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        // Message
        Text(
            text = reminder.reminderMessage,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(reminderMsg) {
                linkTo(
                    start = parent.start,
                    end = reminderDate.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 10.dp)
                bottom.linkTo(parent.bottom, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = reminder.reminderDate.toDateString(),
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 14.sp,
            modifier = Modifier.constrainAs(reminderDate) {
                linkTo(
                    start = reminderMsg.end,
                    end = parent.end,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 10.dp)
                bottom.linkTo(parent.bottom, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )
    }
}

private fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))
}
