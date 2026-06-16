package com.example.lokastay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lokastay.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(
        email: String
    ): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("""
        SELECT * FROM users
        WHERE email = :email
        AND password = :password
    """)
    suspend fun login(
        email: String,
        password: String
    ): User?

    @Update
    suspend fun update(
        user: User
    )

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}