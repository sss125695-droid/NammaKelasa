package com.example.nammakelasa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakelasa.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(repository.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() {
        _error.value = null
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            _error.value = "Email and password are required"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.signIn(trimmedEmail, password)
                .onSuccess {
                    _user.value = it
                    Log.d("AuthViewModel", "Sign in success: ${it?.uid}")
                    onSuccess()
                }
                .onFailure {
                    val msg = when (it) {
                        is FirebaseAuthException -> {
                            Log.e("AuthViewModel", "Auth error code: ${it.errorCode}")
                            when (it.errorCode) {
                                "ERROR_INVALID_EMAIL", "invalid-email" -> "Invalid email format."
                                "ERROR_WRONG_PASSWORD", "wrong-password" -> "Incorrect password."
                                "ERROR_USER_NOT_FOUND", "user-not-found" -> "No account found with this email."
                                "ERROR_USER_DISABLED", "user-disabled" -> "This account has been disabled."
                                "ERROR_TOO_MANY_REQUESTS", "too-many-requests" -> "Too many attempts. Try again later."
                                "ERROR_OPERATION_NOT_ALLOWED", "operation-not-allowed" -> "Login is disabled."
                                "ERROR_INVALID_CREDENTIAL", "invalid-credential" -> "Invalid email or password. If you just registered, please wait a moment."
                                else -> it.localizedMessage ?: "Login Failed"
                            }
                        }
                        is FirebaseNetworkException -> {
                            "Network error: Please check your internet connection or DNS settings. (UnknownHostException)"
                        }
                        else -> it.localizedMessage ?: "Login Failed"
                    }
                    _error.value = msg
                    Log.e("AuthViewModel", "Sign in failure: $msg", it)
                }
            _loading.value = false
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            _error.value = "Email and password are required"
            return
        }
        if (password.length < 6) {
            _error.value = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.signUp(trimmedEmail, password)
                .onSuccess {
                    _user.value = it
                    Log.d("AuthViewModel", "Sign up success: ${it?.uid}")
                    onSuccess()
                }
                .onFailure {
                    val msg = when (it) {
                        is FirebaseAuthException -> {
                            Log.e("AuthViewModel", "Auth error code: ${it.errorCode}")
                            when (it.errorCode) {
                                "ERROR_EMAIL_ALREADY_IN_USE", "email-already-in-use" -> "An account already exists with this email."
                                "ERROR_WEAK_PASSWORD", "weak-password" -> "The password is too weak."
                                "ERROR_INVALID_EMAIL", "invalid-email" -> "Malformed email address."
                                else -> it.localizedMessage ?: "Registration Failed"
                            }
                        }
                        is FirebaseNetworkException -> {
                            "Network error: DNS resolution failed. Check your device's internet connection."
                        }
                        else -> it.localizedMessage ?: "Registration Failed"
                    }
                    _error.value = msg
                    Log.e("AuthViewModel", "Sign up failure: $msg", it)
                }
            _loading.value = false
        }
    }

    fun signOut() {
        repository.signOut()
        _user.value = null
        _error.value = null
    }
}
