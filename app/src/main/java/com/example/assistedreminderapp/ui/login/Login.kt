package com.example.assistedreminderapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.ui.Screen
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch

suspend fun loginOnClick(viewModel: LoginViewModel, showHomeScreen: (userId: Long) -> Unit, username : String, password : String)
{
    val user = User(username = username, password = password)
    if(viewModel.isLoginInfoValid(user))
    {
        val userId = viewModel.findUserIdByName(username)
        if(userId != null)
        {
            showHomeScreen(userId)
        }
    }
}

@Composable
fun Login(
    viewModel: LoginViewModel = viewModel(),
    showHomeScreen: (userId: Long) -> Unit,
)
{
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        val username = rememberSaveable{ mutableStateOf( "") }
        val password = rememberSaveable{ mutableStateOf( "") }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Assisted Reminders",
                    fontSize = 35.sp,
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                Text(
                    text = "Sign in to your account",
                    fontSize = 24.sp,
                )

            }
            Spacer(
                modifier = Modifier.height(17.dp)
            )
            /* Username field */
            TextField(
                value = username.value,
                singleLine = true,
                onValueChange = { data -> username.value = data},
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                ,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus()}
                ),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            /* Password field */
            TextField(
                value = password.value,
                singleLine = true,
                onValueChange = { data -> password.value = data},
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                ,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus()}
                ),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        loginOnClick(
                            viewModel = viewModel,
                            showHomeScreen = showHomeScreen,
                            username = username.value,
                            password = password.value,
                        )
                    }
                },
                enabled = true,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                )

            ) {
                Text(
                    text = "Login",
                    fontSize = 24.sp,
                )
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = "LoginArrow",
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(
                text = "Invalid credentials",
                fontSize = 24.sp,
                color = if(viewState.loginFailedError) Color.Red else Color.Transparent
            )
        }
    }
}