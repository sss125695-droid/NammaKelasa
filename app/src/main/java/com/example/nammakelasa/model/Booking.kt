package com.example.nammakelasa.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Booking(
    val bookingId: String = "",
    val workerId: String = "",
    val customerId: String = "",
    val status: String = "Pending", // Pending, Accepted, Completed
    val jobDescription: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
