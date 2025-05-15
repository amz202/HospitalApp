package com.example.hospitalapp.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hospitalapp.HospitalApplication
import com.example.hospitalapp.data.repositories.MessageRepository
import com.example.hospitalapp.network.model.MessageRequest
import com.example.hospitalapp.network.model.MessageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    var messagesUiState: BaseUiState<List<MessageResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    var conversationUiState: BaseUiState<List<MessageResponse>> by mutableStateOf(BaseUiState.Loading)
        private set

    private val _messages = MutableStateFlow<List<MessageResponse>>(emptyList())
    val messages: StateFlow<List<MessageResponse>> = _messages

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    fun getUserMessages(userId: Long) {
        viewModelScope.launch {
            messagesUiState = BaseUiState.Loading
            try {
                val result = messageRepository.getUserMessages(userId)
                _messages.value = result
                messagesUiState = BaseUiState.Success(result)
            } catch (e: Exception) {
                messagesUiState = BaseUiState.Error
            }
        }
    }

    fun getConversation(userId1: Long, userId2: Long) {
        viewModelScope.launch {
            conversationUiState = BaseUiState.Loading
            try {
                val result = messageRepository.getConversation(userId1, userId2)
                conversationUiState = BaseUiState.Success(result)

                // Mark messages as read when conversation is opened
                messageRepository.markMessagesAsRead(userId1, userId2)

                // Update unread count after marking messages as read
                updateUnreadCount(userId1)
            } catch (e: Exception) {
                conversationUiState = BaseUiState.Error
            }
        }
    }

    fun sendMessage(senderId: Long, receiverId: Long, content: String, attachment: String? = null) {
        viewModelScope.launch {
            try {
                val messageRequest = MessageRequest(
                    senderId = senderId,
                    receiverId = receiverId,
                    content = content,
                    attachment = attachment
                )

                messageRepository.sendMessage(messageRequest)

                // Refresh the conversation
                getConversation(senderId, receiverId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateUnreadCount(userId: Long) {
        viewModelScope.launch {
            try {
                val count = messageRepository.getUnreadMessagesCount(userId)
                _unreadCount.value = count
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HospitalApplication)
                MessageViewModel(
                    messageRepository = application.container.messageRepository
                )
            }
        }
    }
}