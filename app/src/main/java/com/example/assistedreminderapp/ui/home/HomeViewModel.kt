package com.example.assistedreminderapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.data.repository.ReminderRepository
import com.example.assistedreminderapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
   private val userRepository: UserRepository = Graph.userRepository
) : ViewModel()
{
   private val _state = MutableStateFlow(HomeViewState())

   val state: StateFlow<HomeViewState>
      get() = _state

   init {
      viewModelScope.launch {

      }
   }

}

data class HomeViewState(
   //TODO:: ADD STATE ELEMENTS
   val user: User? = null
)