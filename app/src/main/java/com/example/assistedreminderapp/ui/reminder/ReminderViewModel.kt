package com.example.assistedreminderapp.ui.reminder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.Graph.reminderRepository
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    userId: Long,
    reminderId: Long? = null,
    userRepository: UserRepository = Graph.userRepository,
    reminderRepository: ReminderRepository = Graph.reminderRepository,
) : ViewModel()
{
    private val _state = MutableStateFlow(ReminderViewState());
    private val _reminder = MutableStateFlow<Reminder?>(null)

    val state: MutableStateFlow<ReminderViewState>
        get() = _state

    suspend fun addReminder(reminder: Reminder)
    {
        viewModelScope.launch {
            reminderRepository.addReminder(reminder)
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

data class ReminderViewState(
    val reminder: Reminder? = null,
)