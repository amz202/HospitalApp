package com.example.hospitalapp.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalapp.data.datastore.UserPreferences
import com.example.hospitalapp.network.model.UserResponse
import com.example.hospitalapp.network.model.UserRole
import com.example.hospitalapp.ui.navigation.DoctorDashboardNav
import com.example.hospitalapp.ui.navigation.PatientDashboardNav
import com.example.hospitalapp.ui.viewModels.BaseUiState
import com.example.hospitalapp.ui.viewModels.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    userViewModel: UserViewModel,
    userPreferences: UserPreferences,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(UserRole.DOCTOR) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userViewModel.getUsersByRole(UserRole.DOCTOR)
        userViewModel.getUsersByRole(UserRole.PATIENT)
    }

    var isLoading by remember { mutableStateOf(false) }

    var doctorUsers by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var patientUsers by remember { mutableStateOf<List<UserResponse>>(emptyList()) }

    val usersListState = userViewModel.usersListUiState

    LaunchedEffect(usersListState) {
        if (usersListState is BaseUiState.Success) {
            if (usersListState.data.isNotEmpty()) {
                val firstUser = usersListState.data.first()
                when (firstUser.role) {
                    "DOCTOR" -> doctorUsers = usersListState.data
                    "PATIENT" -> patientUsers = usersListState.data
                }
            }
            isLoading = false
        } else if (usersListState is BaseUiState.Loading) {
            isLoading = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = if (selectedTab == UserRole.DOCTOR) 0 else 1
            ) {
                Tab(
                    selected = selectedTab == UserRole.DOCTOR,
                    onClick = {
                        selectedTab = UserRole.DOCTOR
                        if (doctorUsers.isEmpty()) {
                            coroutineScope.launch {
                                userViewModel.getUsersByRole(UserRole.DOCTOR)
                            }
                        }
                    },
                    text = { Text("Doctors") }
                )

                Tab(
                    selected = selectedTab == UserRole.PATIENT,
                    onClick = {
                        selectedTab = UserRole.PATIENT
                        if (patientUsers.isEmpty()) {
                            coroutineScope.launch {
                                userViewModel.getUsersByRole(UserRole.PATIENT)
                            }
                        }
                    },
                    text = { Text("Patients") }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        val currentUsers = if (selectedTab == UserRole.DOCTOR) doctorUsers else patientUsers

                        if (currentUsers.isEmpty()) {
                            Text(
                                text = "No ${selectedTab.name.lowercase()}s found",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        } else {
                            UsersList(
                                users = currentUsers,
                                onUserClick = { user ->
                                    coroutineScope.launch {
                                        userPreferences.saveUser(user)

                                        when (user.role) {
                                            "DOCTOR" -> navController.navigate(DoctorDashboardNav(user.id))
                                            "PATIENT" -> navController.navigate(PatientDashboardNav(user.id))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsersList(
    users: List<UserResponse>,
    onUserClick: (UserResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            UserItem(user = user, onClick = { onUserClick(user) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserItem(
    user: UserResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "User ID: ${user.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Role: ${user.role}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Created: ${user.accountCreationDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(onClick = onClick) {
                Text("Login As")
            }
        }
    }
}