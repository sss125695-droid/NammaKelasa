package com.example.nammakelasa.ui.screens

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.nammakelasa.model.Worker
import com.example.nammakelasa.ui.components.AppButton
import com.example.nammakelasa.ui.components.AppTextField
import com.example.nammakelasa.utils.LocationHelper
import com.example.nammakelasa.utils.NetworkUtils
import com.example.nammakelasa.viewmodel.AuthViewModel
import com.example.nammakelasa.viewmodel.WorkerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkerRegistrationScreen(
    authViewModel: AuthViewModel,
    workerViewModel: WorkerViewModel,
    onRegistrationSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var skillType by remember { mutableStateOf("Painter") }
    var dailyRate by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val skills = listOf("Painter", "Electrician", "Plumber", "Gardener", "Carpenter", "Tiler")
    var expanded by remember { mutableStateOf(false) }
    
    val loading by authViewModel.loading.collectAsState()
    val authError by authViewModel.error.collectAsState()
    val workerError by workerViewModel.error.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }

    LaunchedEffect(authError) {
        authError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(workerError) {
        workerError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Worker Registration",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Profile Image Picker
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { imagePicker.launch("image/*") }
        ) {
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Photo", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = name, onValueChange = { name = it }, label = "Full Name")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // Skill Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = skillType,
                onValueChange = { },
                label = { Text("Skill Type") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                skills.forEach { skill ->
                    DropdownMenuItem(
                        text = { Text(skill) },
                        onClick = {
                            skillType = skill
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(value = dailyRate, onValueChange = { dailyRate = it }, label = "Daily Wage (INR)")
        Spacer(modifier = Modifier.height(12.dp))
        
        // Location Field with GPS button
        OutlinedTextField(
            value = locationName,
            onValueChange = { locationName = it },
            label = { Text("Location / Area") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    if (locationPermissionState.status.isGranted) {
                        if (locationHelper.isLocationEnabled()) {
                            scope.launch {
                                val loc = locationHelper.getCurrentLocation()
                                if (loc != null) {
                                    latitude = loc.latitude
                                    longitude = loc.longitude
                                    locationName = locationHelper.getAddressFromLocation(loc.latitude, loc.longitude)
                                    Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e("WorkerRegistration", "Failed to get location from helper")
                                    Toast.makeText(context, "Could not get precise location. Try again or enter manually.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.w("WorkerRegistration", "Location services are disabled")
                            Toast.makeText(context, "Location services are disabled. Please enable GPS.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.i("WorkerRegistration", "Requesting location permission")
                        locationPermissionState.launchPermissionRequest()
                    }
                }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Get GPS Location")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            AppButton(
                text = "Register",
                onClick = {
                    Log.d("WorkerRegistration", "Register button clicked")
                    
                    if (!NetworkUtils.isNetworkAvailable(context)) {
                        Toast.makeText(context, "No network connection. Please enable Wi-Fi or Mobile Data.", Toast.LENGTH_LONG).show()
                        return@AppButton
                    }

                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && dailyRate.isNotEmpty()) {
                        if (password.length < 6) {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@AppButton
                        }
                        
                        authViewModel.signUp(email, password) {
                            val currentUser = authViewModel.user.value
                            Log.d("WorkerRegistration", "Auth signUp success, user: ${currentUser?.uid}")
                            if (currentUser != null) {
                                val worker = Worker(
                                    workerId = currentUser.uid,
                                    name = name,
                                    phone = phone,
                                    email = email,
                                    skillType = skillType,
                                    jobType = skillType, // Keep both in sync if both are used
                                    dailyRate = dailyRate.toDoubleOrNull() ?: 0.0,
                                    locationName = locationName,
                                    latitude = latitude,
                                    longitude = longitude
                                )
                                workerViewModel.registerWorker(worker) {
                                    Log.d("WorkerRegistration", "Worker Firestore registration success")
                                    if (profileImageUri != null) {
                                        Log.d("WorkerRegistration", "Uploading profile image...")
                                        workerViewModel.updateWorkerProfile(worker, profileImageUri) {
                                            Log.d("WorkerRegistration", "Profile image upload success")
                                            onRegistrationSuccess()
                                        }
                                    } else {
                                        onRegistrationSuccess()
                                    }
                                }
                            } else {
                                Log.e("WorkerRegistration", "User null after signUp")
                                Toast.makeText(context, "User session not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}
