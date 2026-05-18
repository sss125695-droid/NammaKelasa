package com.example.nammakelasa.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakelasa.ui.components.AppButton
import com.example.nammakelasa.ui.components.AppTextField
import com.example.nammakelasa.utils.NetworkUtils
import com.example.nammakelasa.viewmodel.AuthViewModel

@Composable
fun WorkerLoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Worker Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            isError = error != null,
            errorMessage = error
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isError = error != null,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        if (loading) {
            CircularProgressIndicator()
        } else {
            AppButton(
                text = "Login",
                onClick = {
                    if (!NetworkUtils.isNetworkAvailable(context)) {
                        Toast.makeText(context, "No network connection.", Toast.LENGTH_SHORT).show()
                        return@AppButton
                    }
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.signIn(email, password, onLoginSuccess)
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        
        TextButton(onClick = onNavigateToSignUp) {
            Text("Don't have an account? Register")
        }
    }
}
