package com.example.nammakelasa.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class Job(
    var jobId: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var customerId: String = "",
    @get:PropertyName("userid") @set:PropertyName("userid")
    var userId: String = "",
    @get:PropertyName("worker id") @set:PropertyName("worker id")
    var workerId: String = "",
    var location: String = "",
    var budget: Any? = null,
    var status: String = "Open", // Open, In Progress, Completed
    @ServerTimestamp
    var createdAt: Date? = null
) {
    @get:Exclude
    val budgetAmount: Double
        get() {
            val currentBudget = budget
            return when (currentBudget) {
                is Number -> currentBudget.toDouble()
                is String -> currentBudget.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }
}
