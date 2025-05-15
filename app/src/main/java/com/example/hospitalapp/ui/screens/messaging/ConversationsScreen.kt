package com.example.hospitalapp.ui.screens.messaging

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.MessageViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    onNavigateToChat: (userId: Long, userName: String) -> Unit,
    onNavigateBack: () -> Unit,
    messageViewModel: MessageViewModel = viewModel(factory = MessageViewModel.Factory)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = UserPreferences(context)
    var currentUser by remember { mutableStateOf<UserPreferences.UserInfo?>(null) }
    val messagesUiState = messageViewModel.messagesUiState

    LaunchedEffect(Unit) {
        // Load user info once when the screen is first displayed
        currentUser = userPreferences.getUser()
        currentUser?.id?.let { userId ->
            messageViewModel.getUserMessages(userId)
            messageViewModel.updateUnreadCount(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (messagesUiState) {
                is BaseUiState.Loading -> {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                        Text("Loading")
                    }
                }
                is BaseUiState.Error -> {
                    ErrorMessage()
                }
                is BaseUiState.Success -> {
                    val messages = messagesUiState.data
                    val currentUserId = currentUser?.id ?: 0

                    // Group messages by conversation (sender/receiver pair)
                    val conversations = messages
                        .groupBy { msg ->
                            if (msg.senderId == currentUserId) msg.receiverId else msg.senderId
                        }
                        .map { (userId, messages) ->
                            val userName = if (messages.first().senderId == currentUserId) {
                                messages.first().receiverName
                            } else {
                                messages.first().senderName
                            }
                            val lastMessage = messages.maxByOrNull { it.timestamp }
                            val unreadCount = messages.count { !it.isRead && it.receiverId == currentUserId }

                            ConversationItem(userId, userName, lastMessage?.content ?: "", lastMessage?.timestamp ?: "", unreadCount)
                        }
                        .sortedByDescending { it.lastMessageTime }

                    if (conversations.isEmpty()) {
                        EmptyConversations()
                    } else {
                        ConversationsList(conversations) { userId, userName ->
                            onNavigateToChat(userId, userName)
                        }
                    }
                }
            }
        }
    }
}

data class ConversationItem(
    val userId: Long,
    val userName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConversationsList(
    conversations: List<ConversationItem>,
    onConversationClick: (userId: Long, userName: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(conversations) { conversation ->
            ConversationListItem(conversation) {
                onConversationClick(conversation.userId, conversation.userName)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListItem(
    conversation: ConversationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar or initial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.userName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )

                    Text(
                        text = formatMessageTime(conversation.lastMessageTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyConversations() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Chat,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No conversations yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Messages from doctors and patients will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun ErrorMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Failed to load messages")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatMessageTime(timestamp: String): String {
    // You can implement a more sophisticated time formatting function
    // This is a simple version that assumes timestamp format: "yyyy-MM-dd HH:mm:ss"
    try {
        val dateTime = LocalDateTime.parse(
            timestamp,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
        val today = LocalDate.now()

        return when {
            dateTime.toLocalDate().isEqual(today) -> {
                // If today, show time only
                dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            dateTime.toLocalDate().isEqual(today.minusDays(1)) -> {
                // If yesterday, show "Yesterday"
                "Yesterday"
            }
            dateTime.toLocalDate().year == today.year -> {
                // If same year, show day and month
                dateTime.format(DateTimeFormatter.ofPattern("d MMM"))
            }
            else -> {
                // Otherwise show date
                dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
            }
        }
    } catch (e: Exception) {
        return timestamp
    }
}