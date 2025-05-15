package com.example.hospitalapp.network.model

data class MessageRequest(
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val attachment: String? = null
)
data class MessageResponse(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long,
    val receiverName: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean,
    val attachment: String? = null
)