package com.example.nammakelasa.repository

import com.example.nammakelasa.firebase.FirebaseModule
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseModule.auth

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(result.user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        Result.success(result.user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun signOut() {
        auth.signOut()
    }
}
