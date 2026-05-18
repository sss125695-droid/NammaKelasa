package com.example.nammakelasa.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class Review(
    var reviewId: String = "",
    var workerId: String = "",
    var customerId: String = "",
    var customerName: String = "",
    var rating: Any? = null,
    var comment: String = "",
    @ServerTimestamp
    var timestamp: Date? = null
) {
    @get:Exclude
    val ratingValue: Float
        get() {
            val r = rating
            return when (r) {
                is Number -> r.toFloat()
                is String -> r.toFloatOrNull() ?: 0f
                else -> 0f
            }
        }
}
