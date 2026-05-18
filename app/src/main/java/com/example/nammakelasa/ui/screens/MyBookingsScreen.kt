package com.example.nammakelasa.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakelasa.model.Booking
import com.example.nammakelasa.viewmodel.AuthViewModel
import com.example.nammakelasa.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    viewModel: BookingViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val user by authViewModel.user.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            // By default, we fetch as customer. In a real app, you'd determine the role.
            viewModel.fetchBookings(it.uid, isWorker = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Bookings") })
        }
    ) { padding ->
        if (loading && bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookings found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Booking ID: ${booking.bookingId}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = booking.jobDescription, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Status: ${booking.status}",
                    color = when (booking.status) {
                        "Pending" -> MaterialTheme.colorScheme.primary
                        "Accepted" -> MaterialTheme.colorScheme.tertiary
                        "Completed" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}
