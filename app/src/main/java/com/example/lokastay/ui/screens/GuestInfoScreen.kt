package com.example.lokastay.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lokastay.ui.theme.MainCyan
import com.example.lokastay.viewmodel.TransactionViewModel

@Composable
fun GuestInfoScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val transactionState by transactionViewModel.uiState.collectAsState()

    var name by remember { mutableStateOf(transactionState.draftName) }
    var email by remember { mutableStateOf(transactionState.draftEmail) }
    var phone by remember { mutableStateOf(transactionState.draftPhone) }

    LaunchedEffect(name, email, phone) {
        transactionViewModel.saveDraftGuest(name, email, phone)
    }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormComplete = name.isNotBlank() && email.isNotBlank() && isEmailValid && phone.isNotBlank()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 32.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp, bottom = 32.dp)) {
                    Button(
                        onClick = onNextClick,
                        enabled = isFormComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainCyan,
                            disabledContainerColor = Color(0xFFEEEEEE),
                            disabledContentColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text(text = "Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 4.dp, bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(48.dp)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }

                    Text(
                        text = "Booking and Payment",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 24.dp).verticalScroll(rememberScrollState())) {

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBackClick() }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFEEEEEE)), contentAlignment = Alignment.Center) {
                            Text("1", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Booking", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = Color(0xFFEEEEEE))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MainCyan), contentAlignment = Alignment.Center) {
                            Text("2", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guest Info", color = MainCyan, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 4.dp), color = Color(0xFFEEEEEE))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = isFormComplete) { onNextClick() }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    ) {
                        Text("3", color = if (isFormComplete) Color.Gray else Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Payment", color = if (isFormComplete) Color.Gray else Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Name", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Enter your name", color = Color.Gray, fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Outlined.PersonOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedBorderColor = MainCyan,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Email", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter your email", color = Color.Gray, fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedBorderColor = MainCyan,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Phone Number", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("Enter your phone number", color = Color.Gray, fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedBorderColor = MainCyan,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}