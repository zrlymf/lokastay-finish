package com.example.lokastay.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val villaId: Int,
    val collectionId: Int = 0
)