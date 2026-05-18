package com.example.nammakelasa.repository

import com.example.nammakelasa.firebase.FirebaseModule
import com.example.nammakelasa.model.Review
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val reviewsCollection = FirebaseModule.firestore.collection("reviews")

    suspend fun addReview(review: Review): Result<Unit> = try {
        val docRef = reviewsCollection.document()
        val reviewWithId = review.copy(reviewId = docRef.id)
        docRef.set(reviewWithId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getReviewsForWorker(workerId: String): Flow<List<Review>> = callbackFlow {
        val subscription = reviewsCollection
            .whereEqualTo("workerId", workerId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<Review>()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(reviews)
                }
            }
        awaitClose { subscription.remove() }
    }
}
