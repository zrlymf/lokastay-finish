package com.example.lokastay.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val isLoggedIn: Boolean = false,
    val memberNumber: String = "",
    val currentPoints: Int = 0,
    val accumulatedPoints: Int = 0,
    val memberTier: String = "Classic"
)