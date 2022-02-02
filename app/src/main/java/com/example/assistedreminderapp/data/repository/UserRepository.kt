package com.example.assistedreminderapp.data.repository

import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.data.room.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun users(): Flow<List<User>> = userDao.getUsers()
    suspend fun getUserByName(username: String) = userDao.getUserByName(username)
    suspend fun getUserById(userId: Long) = userDao.getUserById(userId)

    suspend fun addUser(user: User): Long
    {
        return when (val l = userDao.getUserByName(user.username))
        {
            //NOTE:: ADDS USER IF IT DOES NOT ALREADY EXIST, OTHERWISE RETURNS OLD ONE'S ID
            null->userDao.insert(user)
            else->l.id
        }
    }
}