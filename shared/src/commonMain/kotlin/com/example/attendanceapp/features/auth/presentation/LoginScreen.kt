package com.example.attendanceapp.features.auth.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendanceapp.features.auth.domain.User

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (User) -> Unit
) {
    val bgColor = Color(0xFFF6F8ED)
    val darkColor = Color(0xFF2C3127)
    val neonGreen = Color(0xFFB9F633)
    val inputBgColor = Color(0xFFEAEEDB)
    val borderColor = Color(0xFFD4D9C5)
    val errorColor = Color(0xFFE53935)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val user = (uiState as LoginUiState.Success).user
            onLoginSuccess(user)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(color = neonGreen, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = "Logo",
                    tint = darkColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to Lumina EdTech",
                fontSize = 16.sp,
                color = darkColor.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email or Username",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter your email") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, contentDescription = "Email icon")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = darkColor,
                        unfocusedTextColor = darkColor
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkColor
                    )
                    Text(
                        text = "Forgot Password?",
                        fontSize = 12.sp,
                        color = darkColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Enter your password") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = "Lock icon")
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = darkColor,
                        unfocusedTextColor = darkColor
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val currentState = uiState
            if (currentState is LoginUiState.Error) {
                Text(
                    text = currentState.message,
                    color = errorColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = darkColor),
                enabled = currentState !is LoginUiState.Loading
            ) {
                if (currentState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = neonGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Login",
                            color = neonGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Login Arrow",
                            tint = neonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = inputBgColor,
                    contentColor = darkColor
                ),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric",
                    tint = darkColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Face ID / Biometric Login",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HeadsetMic,
                    contentDescription = "Support",
                    tint = darkColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Contact Administrator",
                    fontSize = 14.sp,
                    color = darkColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
