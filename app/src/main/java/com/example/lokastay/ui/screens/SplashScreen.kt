package com.example.lokastay.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lokastay.R
import com.example.lokastay.ui.theme.HoverCyan
import com.example.lokastay.ui.theme.BorderCyan
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {

    LaunchedEffect(key1 = true) {
        delay(2500L)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BorderCyan),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxRadius = size.height
            val step = size.width / 4f
            var currentRadius = maxRadius

            while (currentRadius > 0) {
                drawCircle(
                    color = HoverCyan.copy(alpha = 0.2f),
                    radius = currentRadius,
                    center = center
                )
                currentRadius -= step
            }
        }

        Image(
            painter = painterResource(id = R.drawable.logo_splash),
            contentDescription = "Logo Lokastay",
            modifier = Modifier.width(280.dp)
        )
    }
}