package com.example.hospitalapp.data.repositories
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.MessageDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.MessageEntity
import com.example.hospitalapp.data.local.extensions.toMessageResponse
import com.example.hospitalapp.network.model.MessageRequest
import com.example.hospitalapp.network.model.MessageResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface MessageRepository {
    suspend fun getMessageById(id: Long): MessageResponse
    suspend fun getUserMessages(userId: Long): List<MessageResponse>
    suspend fun getConversation(userId1: Long, userId2: Long): List<MessageResponse>
    suspend fun sendMessage(messageRequest: MessageRequest): MessageResponse
    suspend fun markMessagesAsRead(userId: Long, otherUserId: Long)
    suspend fun getUnreadMessagesCount(userId: Long): Int
    suspend fun getUnreadMessageSenders(userId: Long): List<Long>
}

class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val userDao: UserDao
) : MessageRepository {

    override suspend fun getMessageById(id: Long): MessageResponse {
        return messageDao.getMessageById(id)?.toMessageResponse(userDao)
            ?: throw IllegalStateException("Message not found")
    }

    override suspend fun getUserMessages(userId: Long): List<MessageResponse> {
        return messageDao.getUserMessages(userId).map { it.toMessageResponse(userDao) }
    }

    override suspend fun getConversation(userId1: Long, userId2: Long): List<MessageResponse> {
        return messageDao.getConversation(userId1, userId2).map { it.toMessageResponse(userDao) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun sendMessage(messageRequest: MessageRequest): MessageResponse {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val messageEntity = MessageEntity(
            senderId = messageRequest.senderId,
            receiverId = messageRequest.receiverId,
            content = messageRequest.content,
            timestamp = timestamp,
            isRead = false,
            attachment = messageRequest.attachment
        )

        val id = messageDao.insertMessage(messageEntity)
        return getMessageById(id)
    }

    override suspend fun markMessagesAsRead(userId: Long, otherUserId: Long) {
        messageDao.markMessagesAsRead(userId, otherUserId)
    }

    override suspend fun getUnreadMessagesCount(userId: Long): Int {
        return messageDao.getUnreadMessagesCount(userId)
    }

    override suspend fun getUnreadMessageSenders(userId: Long): List<Long> {
        return messageDao.getUnreadMessageSenders(userId)
    }
}