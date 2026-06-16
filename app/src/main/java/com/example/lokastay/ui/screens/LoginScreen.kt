package com.example.lokastay.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokastay.R
import com.example.lokastay.ui.theme.*
import com.example.lokastay.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoggedIn, uiState.message) {
        if (uiState.isLoggedIn) {
            if (uiState.message == "Login successful" || uiState.message == "User already logged in") {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        } else if (uiState.message.isNotEmpty()) {
            if (uiState.message != "Not logged in" && uiState.message != "Logout successful") {
                Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral10)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_pattern),
            contentDescription = "Background Pattern",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_auth),
                    contentDescription = "Logo Lokastay Auth",
                    modifier = Modifier
                        .height(46.dp)
                        .offset(x = (-15).dp),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral100
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Login to your account to explore about our app",
                    color = Neutral70,
                    fontSize = 14.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Neutral10,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 12.dp)
                ) {
                    CustomTextField(value = email, onValueChange = { email = it }, label = "Email", placeholder = "Enter your email")
                    CustomTextField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "Enter your password", isPassword = true)

                    Text(
                        text = "Forgot Password?",
                        color = MainCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 24.dp)
                            .clickable { /* TODO: Forgot Password */ }
                    )

                    PrimaryButton(
                        text = "Login",
                        enabled = isFormValid,
                        onClick = {
                            viewModel.login(email, password)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Neutral30)
                        Text(
                            text = "Or",
                            color = Neutral50,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = 14.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Neutral30)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SocialButton(text = "Google", iconResId = R.drawable.ic_google, modifier = Modifier.weight(1f))
                        SocialButton(text = "Facebook", iconResId = R.drawable.ic_facebook, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Don't have an account? ", color = Neutral70, fontSize = 14.sp)
                        Text(
                            text = "Register",
                            color = MainCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}