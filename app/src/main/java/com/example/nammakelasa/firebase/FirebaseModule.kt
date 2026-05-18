package com.example.nammakelasa.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseModule {
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage get() = FirebaseStorage.getInstance()

    const val WORKERS_COLLECTION = "workers"
    const val USERS_COLLECTION = "users"
    const val BOOKINGS_COLLECTION = "bookings"
    const val JOBS_COLLECTION = "jobs"
}
