package com.ahmedadeltito.chatapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Data Access Object for MessageEntity
@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace if message with same ID exists
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getMessages(): Flow<List<MessageEntity>> // Returns a Flow of entities

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE isSentByMe = 1 AND status = :status ORDER BY timestamp ASC")
    suspend fun getOutgoingMessagesByStatus(status: com.ahmedadeltito.chatapp.domain.MessageStatus): List<MessageEntity>

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
} 