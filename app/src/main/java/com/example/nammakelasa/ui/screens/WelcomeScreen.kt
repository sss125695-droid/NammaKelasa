package com.example.nammakelasa.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakelasa.ui.components.AppButton

@Composable
fun WelcomeScreen(
    onWorkerLogin: () -> Unit,
    onCustomerBrowse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Namma-Kelsa",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Empowering skilled workers, connecting communities.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        AppButton(
            text = "I am a Worker",
            onClick = onWorkerLogin
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppButton(
            text = "I am a Customer",
            onClick = onCustomerBrowse,
            containerColor = MaterialTheme.colorScheme.secondary
        )
    }
}
