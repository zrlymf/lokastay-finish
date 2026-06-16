package com.example.lokastay.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val villaId: Int,

    val username: String,

    val comment: String,

    val rating: Float
)