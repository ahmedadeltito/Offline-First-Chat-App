package com.ahmedadeltito.chatapp.data.local

import androidx.room.TypeConverter
import com.ahmedadeltito.chatapp.domain.MessageStatus

// Type converter for Room to store and retrieve MessageStatus enum
class MessageStatusConverter {
    @TypeConverter
    fun fromMessageStatus(value: MessageStatus): String = value.name

    @TypeConverter
    fun toMessageStatus(value: String): MessageStatus = MessageStatus.valueOf(value)
} 