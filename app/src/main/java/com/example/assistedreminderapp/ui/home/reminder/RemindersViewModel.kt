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

   suspend fun saveReminder(reminder: Reminder): Long {
      return reminderRepository.addReminder(reminder)
   }

   //Create mock list of reminders. Later on fetched from Database
   //Currently just list of Reminders, not RemindersFromUser
   private val _reminders = mutableListOf(
      Reminder(
         reminder_user_id = 1,
         reminderMessage = "Wash dishes",
         reminderDate = System.currentTimeMillis()
      ),
      Reminder(
         reminder_user_id = 1,
         reminderMessage = "Take dog out for a walk",
         reminderDate = System.currentTimeMillis()
      ),
      Reminder(
         reminder_user_id = 1,
         reminderMessage = "Call bank",
         reminderDate = System.currentTimeMillis()
      )
   )

   init {
      //Code to fetch from database
//      viewModelScope.launch {
//         reminderRepository.userReminders(userId).collect { list ->
//            _state.value = RemindersViewState(
//               user = userRepository.getUserById(userId),
//               reminders = list
//            )
//         }
//      }
     //Mockup list
      viewModelScope.launch {
         _state.value = RemindersViewState(
            user = userRepository.getUserById(userId),
            reminders = _reminders
         )
      }
   }

   val state: StateFlow<RemindersViewState>
      get() = _state

}

data class RemindersViewState(
   val user: User? = null,
   val reminders: List<Reminder> = emptyList()
)