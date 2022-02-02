package com.example.assistedreminderapp.data.room

import androidx.room.*
import com.example.assistedreminderapp.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {

    //CRUD methods
    @Query(value = "SELECT * FROM user_table WHERE username == :name")
    abstract suspend fun getUserByName(name: String): User?

    @Query(value = "SELECT * FROM user_table WHERE id == :id")
    abstract suspend fun getUserById(id: Long): User?

    @Query(value = "SELECT * FROM user_table")
    abstract fun getUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insert(user: User): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(user: User)

    @Delete
    abstract suspend fun delete(user: User): Int
}