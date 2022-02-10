package com.example.assistedreminderapp.ui.home.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.data.entity.Reminder
import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.repository.UserRepository
import com.example.assistedreminderapp.data.room.RemindersFromUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RemindersViewModel(
   private val userId: Long,
   private val reminderRepository: ReminderRepository = Graph.reminderRepository,
   private val userRepository: UserRepository = Graph.userRepository
) : ViewModel()
{
   private val _state = MutableStateFlow(RemindersViewState())
   private val _showRemovePopup = MutableStateFlow<Boolean>(false)

   //Create mock list of reminders. Later on fetched from Database
   //Currently just list of Reminders, not RemindersFromUser
   private val _reminders = MutableStateFlow<List<RemindersFromUser>>(emptyList())

   init {
      //Code to fetch from database
      viewModelScope.launch {
         reminderRepository.userReminders(userId).collect { list ->
             _reminders.value = list
            _state.value = RemindersViewState(
               user = userRepository.getUserById(userId),
               reminders = list
            )
         }
      }
   }

   fun removeReminder(reminder: Reminder)
   {
      viewModelScope.launch {
          reminderRepository.deleteReminder(reminder)
      }
   }

   val state: StateFlow<RemindersViewState>
      get() = _state

}

data class RemindersViewState(
   val user: User? = null,
   val reminders: List<RemindersFromUser> = emptyList()
)