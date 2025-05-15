package com.example.hospitalapp.ui.screens.messaging

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.ui.viewModels.MessageViewModel
import kotlinx.coroutines.launch

@Composable
fun MessagingFab(
    onClick: () -> Unit,
    messageViewModel: MessageViewModel = viewModel(factory = MessageViewModel.Factory)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = UserPreferences(context)
    var currentUser by remember { mutableStateOf<UserPreferences.UserInfo?>(null) }
    val unreadCount by messageViewModel.unreadCount.collectAsState()

    // Load user data and update unread count
    LaunchedEffect(Unit) {
        scope.launch {
            currentUser = userPreferences.getUser()
            currentUser?.id?.let { userId ->
                messageViewModel.updateUnreadCount(userId)
            }
        }
    }

    BadgedBox(
        badge = {
            if (unreadCount > 0) {
                Badge {
                    Text(text = if (unreadCount > 99) "99+" else unreadCount.toString())
                }
            }
        }
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Messages",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}