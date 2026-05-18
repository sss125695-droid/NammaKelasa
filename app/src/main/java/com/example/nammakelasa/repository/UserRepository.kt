package com.example.nammakelasa.repository

import com.example.nammakelasa.firebase.FirebaseModule
import com.example.nammakelasa.model.User
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val usersCollection = FirebaseModule.firestore.collection(FirebaseModule.USERS_COLLECTION)

    suspend fun createUserProfile(user: User): Result<Unit> = try {
        usersCollection.document(user.userId).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserProfile(userId: String): Result<User?> = try {
        val snapshot = usersCollection.document(userId).get().await()
        Result.success(snapshot.toObject<User>())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
