package com.example.nammakelasa.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.nammakelasa.viewmodel.AuthViewModel
import com.example.nammakelasa.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboard(
    workerViewModel: WorkerViewModel,
    authViewModel: AuthViewModel,
    onUploadGallery: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val user = authViewModel.user.collectAsState().value
    val worker = workerViewModel.currentWorker.collectAsState().value

    LaunchedEffect(user) {
        user?.let { workerViewModel.loadWorker(it.uid) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onUploadGallery) {
                Icon(Icons.Default.Add, contentDescription = "Add Work")
            }
        }
    ) { padding ->
        if (worker == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(worker.profileImage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = worker.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = worker.skillType, color = MaterialTheme.colorScheme.primary)
                        Text(text = "📍 ${worker.locationName}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Wage: ₹${worker.dailyRateAmount}/day", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Availability Status", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = worker.available,
                        onCheckedChange = { workerViewModel.updateAvailability(worker.workerId, it) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Work Gallery", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                if (worker.galleryImages.isEmpty()) {
                    Text(
                        text = "No work images uploaded yet.",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(worker.galleryImages) { imageUrl ->
                            Card(shape = MaterialTheme.shapes.small) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
