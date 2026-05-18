package com.example.nammakelasa.repository

import com.example.nammakelasa.firebase.FirebaseModule
import com.example.nammakelasa.model.Booking
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val bookingsCollection = FirebaseModule.firestore.collection(FirebaseModule.BOOKINGS_COLLECTION)

    suspend fun createBooking(booking: Booking): Result<Unit> = try {
        val docRef = bookingsCollection.document()
        val bookingWithId = booking.copy(bookingId = docRef.id)
        docRef.set(bookingWithId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getBookingsForUser(userId: String, isWorker: Boolean): Flow<List<Booking>> = callbackFlow {
        val query = if (isWorker) {
            bookingsCollection.whereEqualTo("workerId", userId)
        } else {
            bookingsCollection.whereEqualTo("customerId", userId)
        }

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val bookings = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject<Booking>()
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(bookings)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> = try {
        bookingsCollection.document(bookingId).update("status", status).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
