package com.example.assistedreminderapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userId: Long,
    private val userRepository: UserRepository = Graph.userRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(ProfileViewState())

    val state: MutableStateFlow<ProfileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            _state.value = ProfileViewState(
                curUser = userRepository.getUserById(userId)
            )
        }
    }
}

data class ProfileViewState(
    val curUser: User? = null
)