package com.example.lokastay.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "villas")
data class Villa(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val pricePerNight: Double,
    val rating: Float,
    val imageUrl: String,
    val description: String,
    val maxGuest: Int = 2,
    val hasWifi: Boolean = false,
    val hasPool: Boolean = false,
    val hasAc: Boolean = false,
    val hasLaundry: Boolean = false,
    val hasTv: Boolean = false,
    val hasGym: Boolean = false,
    val hasHeater: Boolean = false,
    val hasParking: Boolean = false,
    val hasBbq: Boolean = false,
    val hasBathtub: Boolean = false,
    val hasGarden: Boolean = false,
    val hasKitchen: Boolean = false,
    val imageGallery: List<String> = emptyList()
)