package com.example.nammakelasa.ui.screens

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakelasa.model.Worker
import com.example.nammakelasa.utils.LocationHelper
import com.example.nammakelasa.viewmodel.WorkerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CustomerHomeScreen(
    viewModel: WorkerViewModel,
    onWorkerClick: (String) -> Unit
) {
    val workers by viewModel.workers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }
    
    var userLat by remember { mutableDoubleStateOf(0.0) }
    var userLon by remember { mutableDoubleStateOf(0.0) }
    var isLocationEnabled by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val filteredWorkers = workers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.skillType.contains(searchQuery, ignoreCase = true) ||
                it.locationName.contains(searchQuery, ignoreCase = true)
    }.let { list ->
        if (isLocationEnabled && userLat != 0.0) {
            list.sortedBy { worker ->
                locationHelper.calculateDistance(userLat, userLon, worker.lat, worker.lng)
            }
        } else {
            list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Workers") },
                actions = {
                    IconButton(onClick = {
                        if (locationPermissionState.status.isGranted) {
                            scope.launch {
                                val loc = locationHelper.getCurrentLocation()
                                if (loc != null) {
                                    userLat = loc.latitude
                                    userLon = loc.longitude
                                    isLocationEnabled = true
                                }
                            }
                        } else {
                            locationPermissionState.launchPermissionRequest()
                        }
                    }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Sort by distance",
                            tint = if (isLocationEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by name, skill, or location") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (filteredWorkers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No workers found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredWorkers) { worker ->
                        val distance = if (isLocationEnabled && userLat != 0.0) {
                            locationHelper.calculateDistance(userLat, userLon, worker.lat, worker.lng)
                        } else null
                        
                        WorkerCard(
                            worker = worker, 
                            distance = distance,
                            onClick = { onWorkerClick(worker.workerId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerCard(worker: Worker, distance: Float?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = worker.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = worker.skillType, color = MaterialTheme.colorScheme.primary)
                Text(
                    text = "📍 ${worker.locationName}${distance?.let { " (%.1f km)".format(it) } ?: ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${worker.dailyRateAmount}/day",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (worker.available) {
                    Text("Available", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                } else {
                    Text("Busy", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }
    }
}
