package com.example.nammakelasa.repository

import com.example.nammakelasa.firebase.FirebaseModule
import com.example.nammakelasa.model.Job
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class JobRepository {
    private val jobsCollection = FirebaseModule.firestore.collection(FirebaseModule.JOBS_COLLECTION)

    suspend fun postJob(job: Job): Result<Unit> = try {
        val docRef = if (job.jobId.isEmpty()) jobsCollection.document() else jobsCollection.document(job.jobId)
        val jobToSave = job.copy(jobId = docRef.id)
        docRef.set(jobToSave).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getAllJobs(): Flow<List<Job>> = callbackFlow {
        val subscription = jobsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val jobsList = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject<Job>()
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(jobsList)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun getJob(jobId: String): Result<Job?> = try {
        val snapshot = jobsCollection.document(jobId).get().await()
        val job = try {
            snapshot.toObject<Job>()
        } catch (e: Exception) {
            null
        }
        Result.success(job)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
