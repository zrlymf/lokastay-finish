package com.example.lokastay.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = ",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }
}