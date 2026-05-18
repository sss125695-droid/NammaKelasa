package com.example.nammakelasa.ui.screens

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakelasa.model.Job
import com.example.nammakelasa.ui.components.AppButton
import com.example.nammakelasa.ui.components.AppTextField
import com.example.nammakelasa.utils.LocationHelper
import com.example.nammakelasa.viewmodel.AuthViewModel
import com.example.nammakelasa.viewmodel.JobViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PostJobScreen(
    jobViewModel: JobViewModel,
    authViewModel: AuthViewModel,
    onJobPosted: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }
    val user by authViewModel.user.collectAsState()
    val loading by jobViewModel.loading.collectAsState()

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Post a Job") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AppTextField(value = title, onValueChange = { title = it }, label = "Job Title")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = description, onValueChange = { description = it }, label = "Description")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = category, onValueChange = { category = it }, label = "Category (e.g. Plumbing)")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = budget, onValueChange = { budget = it }, label = "Budget (INR)")
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
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
                                        location = locationHelper.getAddressFromLocation(loc.latitude, loc.longitude)
                                        Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Log.e("PostJob", "Failed to get location")
                                        Toast.makeText(context, "Could not get precise location.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please enable GPS", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            locationPermissionState.launchPermissionRequest()
                        }
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Get GPS Location")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            if (loading) {
                CircularProgressIndicator()
            } else {
                AppButton(
                    text = "Post Job",
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            val newJob = Job(
                                title = title,
                                description = description,
                                category = category,
                                budget = budget.toDoubleOrNull() ?: 0.0,
                                location = location,
                                customerId = user?.uid ?: ""
                            )
                            jobViewModel.postJob(newJob) {
                                Toast.makeText(context, "Job Posted Successfully!", Toast.LENGTH_SHORT).show()
                                onJobPosted()
                            }
                        } else {
                            Toast.makeText(context, "Please fill title and description", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}
