package com.example.assistedreminderapp.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assistedreminderapp.AssistedReminderAppState
import com.example.assistedreminderapp.Graph
import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository = Graph.userRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(LoginViewState())
    private val _loggedInUser = MutableStateFlow<User?>(null)

    var _loginFailedError = mutableStateOf(false)
        private set

    //Try to create the default user
    init {
        viewModelScope.launch{
            userRepository.addUser(User(username="admin", password="123"))
        }
    }

    val state: StateFlow<LoginViewState>
        get() = _state

    suspend fun isLoginInfoValid(user: User): Boolean
    {
       val foundUser = userRepository.getUserByName(user.username)

       if(foundUser != null && user.password == user.password)
       {
           _loggedInUser.value = user
           _state.value = LoginViewState(_loggedInUser.value, false)
            return true
       }
         _loginFailedError.value = true
        _state.value = LoginViewState(_loggedInUser.value, _loginFailedError.value)
       return false
    }

    suspend fun findUserIdByName(username: String): Long?
    {
        val foundUser = userRepository.getUserByName(username)
        if(foundUser != null)
            return foundUser.id
        return null
    }

}

data class LoginViewState(
    val loggedInUser: User? = null,
    val loginFailedError: Boolean = false,
)


