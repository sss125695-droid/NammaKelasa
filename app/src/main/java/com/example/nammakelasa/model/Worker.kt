package com.example.nammakelasa.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class Worker(
    var workerId: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    @get:PropertyName("job Type") @set:PropertyName("job Type")
    var jobType: String = "",
    var skillType: String = "",
    var dailyRate: Any? = null,
    var location: String = "",
    var locationName: String = "",
    var latitude: Any? = null,
    var longitude: Any? = null,
    var available: Boolean = true,
    var experience: String = "",
    @get:PropertyName("jow") @set:PropertyName("jow")
    var job: String = "", // typo from Firestore 'jow'
    var profileImage: String = "",
    var galleryImages: List<String> = emptyList(),
    @ServerTimestamp
    var createdAt: Date? = null
) {
    @get:Exclude
    val dailyRateAmount: Double
        get() {
            val rate = dailyRate
            return when (rate) {
                is Number -> rate.toDouble()
                is String -> rate.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }

    @get:Exclude
    val lat: Double
        get() {
            val l = latitude
            return when (l) {
                is Number -> l.toDouble()
                is String -> l.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }

    @get:Exclude
    val lng: Double
        get() {
            val l = longitude
            return when (l) {
                is Number -> l.toDouble()
                is String -> l.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }
}
