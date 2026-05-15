package com.example.nammakelasa.repository

import android.net.Uri
import com.example.nammakelasa.firebase.FirebaseModule
import com.example.nammakelasa.model.Worker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class WorkerRepository {
    private val firestore = FirebaseModule.firestore
    private val storage = FirebaseModule.storage
    private val workersCollection = firestore.collection(FirebaseModule.WORKERS_COLLECTION)

    suspend fun registerWorker(worker: Worker): Result<Unit> = try {
        workersCollection.document(worker.workerId).set(worker).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getWorker(workerId: String): Result<Worker?> = try {
        val snapshot = workersCollection.document(workerId).get().await()
        val worker = try {
            snapshot.toObject<Worker>()
        } catch (e: Exception) {
            null
        }
        Result.success(worker)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getAllWorkers(): Flow<List<Worker>> = callbackFlow {
        val subscription = workersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val workersList = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject<Worker>()
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(workersList)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateWorkerAvailability(workerId: String, available: Boolean): Result<Unit> = try {
        workersCollection.document(workerId).update("available", available).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun uploadProfileImage(uri: Uri, workerId: String): Result<String> = try {
        val ref = storage.reference.child("profile_images/$workerId.jpg")
        ref.putFile(uri).await()
        val downloadUrl = ref.downloadUrl.await().toString()
        workersCollection.document(workerId).update("profileImage", downloadUrl).await()
        Result.success(downloadUrl)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun uploadWorkImages(uris: List<Uri>, workerId: String): Result<List<String>> = try {
        val imageUrls = mutableListOf<String>()
        for (uri in uris) {
            val fileName = UUID.randomUUID().toString()
            val ref = storage.reference.child("work_gallery/$workerId/$fileName.jpg")
            ref.putFile(uri).await()
            imageUrls.add(ref.downloadUrl.await().toString())
        }
        // Append to existing gallery
        val currentWorker = getWorker(workerId).getOrNull()
        val updatedGallery = (currentWorker?.galleryImages ?: emptyList()) + imageUrls
        workersCollection.document(workerId).update("galleryImages", updatedGallery).await()
        Result.success(imageUrls)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
