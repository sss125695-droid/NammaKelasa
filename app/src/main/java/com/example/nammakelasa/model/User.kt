package com.example.nammakelasa.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var userType: String = "Customer", // "Customer" or "Worker"
    var phoneNumber: String = ""
)
