package com.example.nammakelasa.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nammakelasa.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailScreen(
    workerId: String,
    viewModel: WorkerViewModel,
    onBack: () -> Unit
) {
    val worker by viewModel.currentWorker.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(workerId) {
        viewModel.loadWorker(workerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(worker?.name ?: "Worker Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            worker?.let { w ->
                ExtendedFloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${w.phone}"))
                        context.startActivity(intent)
                    },
                    icon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    text = { Text("Call Worker") }
                )
            }
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            worker?.let { w ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = w.profileImage,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(40.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = w.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(text = w.skillType, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow(label = "Daily Rate", value = "₹${w.dailyRateAmount}")
                    DetailRow(label = "Location", value = w.locationName)
                    DetailRow(label = "Status", value = if (w.available) "Available" else "Busy")

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Work Gallery", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (w.galleryImages.isEmpty()) {
                        Text("No work photos uploaded yet.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(w.galleryImages) { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Work Photo",
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp)),
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

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}
