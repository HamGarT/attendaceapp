package com.example.attendanceapp.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendanceapp.features.profile.presentation.ProfileViewModel

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit = {},
    viewModel: ProfileViewModel
) {
    val backgroundColor = Color(0xFFF7F9ED)
    val profile by viewModel.profile.collectAsState()
    val children by viewModel.children.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val fullName = remember(profile) {
        val name = viewModel.userName
        val lastname = viewModel.userLastname
        if (lastname.isNotBlank()) "$name $lastname" else name
    }

    val email = profile?.email ?: viewModel.userEmail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 120.dp)
    ) {
        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        PersonalInfoCard(
            fullName = fullName,
            email = email,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        FamilyCard(children = children)

        Spacer(modifier = Modifier.height(24.dp))

        NotificationPreferencesCard()

        Spacer(modifier = Modifier.height(32.dp))

        SignOutButton(onClick = onSignOut)
    }
}

@Composable
private fun HeaderSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Profile Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E231C)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Manage your family details and notification preferences.",
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun PersonalInfoCard(
    fullName: String,
    email: String,
    isLoading: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Personal Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E231C)
            )

            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                InfoField(label = "Full Name", value = fullName)
                Spacer(modifier = Modifier.height(16.dp))
                InfoField(label = "Email Address", value = email)

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Surface(
                        color = Color(0xFFEAECE2),
                        shape = RoundedCornerShape(16.dp),
                        onClick = { }
                    ) {
                        Text(
                            text = "Edit Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1E231C),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        Surface(
            color = Color(0xFFF7F9ED),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF1E231C),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun FamilyCard(children: List<com.example.attendanceapp.features.profile.data.ProfileChildWrapper>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Family",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E231C)
            )

            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (children.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No children registered",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                children.forEach { parentChild ->
                    val name = "${parentChild.student.nombres} ${parentChild.student.apellidos}"
                    val details = parentChild.parentesco
                    FamilyMemberItem(name = name, details = details)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun FamilyMemberItem(name: String, details: String) {
    Surface(
        color = Color(0xFFF7F9ED),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E231C))
                    Text(text = details, fontSize = 12.sp, color = Color.DarkGray)
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver mas",
                tint = Color.DarkGray
            )
        }
    }
}

@Composable
private fun NotificationPreferencesCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Notification Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E231C)
            )

            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            PreferenceToggle(
                title = "Arrival Alerts",
                description = "Alert me when my child arrives at school.",
                initialState = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            PreferenceToggle(
                title = "Absence Alerts",
                description = "Notify me immediately if an unexpected absence occurs.",
                initialState = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            PreferenceToggle(
                title = "Weekly Summary",
                description = "Receive a weekly email report of attendance.",
                initialState = false
            )
        }
    }
}

@Composable
private fun PreferenceToggle(title: String, description: String, initialState: Boolean) {
    var isChecked by remember { mutableStateOf(initialState) }

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = Color(0xFF4A6800),
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color.LightGray,
        uncheckedBorderColor = Color.Transparent
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E231C))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, fontSize = 12.sp, color = Color.DarkGray, lineHeight = 16.sp)
        }

        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = switchColors
        )
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C3225),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
