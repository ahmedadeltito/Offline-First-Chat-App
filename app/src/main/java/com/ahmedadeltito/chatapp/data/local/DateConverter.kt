package com.ahmedadeltito.chatapp.data.local

import androidx.room.TypeConverter
import java.util.Date

// Type converter for Room to store and retrieve Date objects
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
} 