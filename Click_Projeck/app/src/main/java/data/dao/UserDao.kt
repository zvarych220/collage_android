package data.dao

import androidx.room.*
import data.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>  // Отримати всіх користувачів

    @Insert
    suspend fun insertUser(user: User)  // Додати користувача

    @Delete
    suspend fun deleteUser(user: User)  // Видалити користувача

    @Update
    suspend fun updateUser(user: User) // Оновити користувача користувача
}