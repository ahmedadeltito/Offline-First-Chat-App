package com.ahmedadeltito.chatapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Room Database definition
@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    MessageStatusConverter::class
) // Register type converters
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
} 