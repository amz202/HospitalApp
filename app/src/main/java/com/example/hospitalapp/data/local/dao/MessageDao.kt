package com.example.hospitalapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hospitalapp.data.local.entities.MessageEntity

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Long): MessageEntity?

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    suspend fun getUserMessages(userId: Long): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY timestamp ASC")
    suspend fun getConversation(userId1: Long, userId2: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :otherUserId AND isRead = 0")
    suspend fun markMessagesAsRead(userId: Long, otherUserId: Long)

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    suspend fun getUnreadMessagesCount(userId: Long): Int

    @Query("SELECT DISTINCT senderId FROM messages WHERE receiverId = :userId AND isRead = 0")
    suspend fun getUnreadMessageSenders(userId: Long): List<Long>
}