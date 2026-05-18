package com.example.nammakelasa.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakelasa.model.Worker
import com.example.nammakelasa.repository.WorkerRepository
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkerViewModel(private val repository: WorkerRepository = WorkerRepository()) : ViewModel() {

    private val _workers = MutableStateFlow<List<Worker>>(emptyList())
    val workers: StateFlow<List<Worker>> = _workers.asStateFlow()

    private val _currentWorker = MutableStateFlow<Worker?>(null)
    val currentWorker: StateFlow<Worker?> = _currentWorker.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllWorkers()
    }

    private fun loadAllWorkers() {
        viewModelScope.launch {
            repository.getAllWorkers().collect {
                _workers.value = it
            }
        }
    }

    fun loadWorker(workerId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.getWorker(workerId).onSuccess {
                if (it != null) {
                    _currentWorker.value = it
                } else {
                    _error.value = "Worker profile not found. Please register as a worker."
                    _currentWorker.value = null
                }
            }.onFailure {
                _error.value = "Failed to load worker profile: ${it.localizedMessage}"
            }
            _loading.value = false
        }
    }

    fun registerWorker(worker: Worker, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.registerWorker(worker)
                .onSuccess {
                    _currentWorker.value = worker
                    Log.d("WorkerViewModel", "Worker registered successfully: ${worker.workerId}")
                    onSuccess()
                }
                .onFailure {
                    _error.value = if (it is FirebaseNetworkException) {
                        "Network Error: Unable to reach database. Check your connection."
                    } else {
                        "Firestore Error: ${it.message}"
                    }
                    Log.e("WorkerViewModel", "Failed to register worker", it)
                }
            _loading.value = false
        }
    }

    fun updateAvailability(workerId: String, available: Boolean) {
        viewModelScope.launch {
            repository.updateWorkerAvailability(workerId, available)
        }
    }

    fun updateWorkerProfile(worker: Worker, profileImageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            if (profileImageUri != null) {
                repository.uploadProfileImage(profileImageUri, worker.workerId)
                    .onSuccess { url ->
                        val updatedWorker = worker.copy(profileImage = url)
                        repository.registerWorker(updatedWorker).onSuccess {
                             _currentWorker.value = updatedWorker
                             onSuccess()
                        }.onFailure {
                            _error.value = "Failed to update profile data"
                            Log.e("WorkerViewModel", "Failed to update profile image in firestore", it)
                        }
                    }
                    .onFailure {
                        _error.value = "Image Upload Failed"
                        Log.e("WorkerViewModel", "Failed to upload profile image", it)
                    }
            } else {
                repository.registerWorker(worker).onSuccess {
                    _currentWorker.value = worker
                    onSuccess()
                }.onFailure {
                    _error.value = "Failed to update profile"
                }
            }
            _loading.value = false
        }
    }

    fun uploadWorkGallery(uris: List<Uri>, workerId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.uploadWorkImages(uris, workerId)
            loadWorker(workerId) // Refresh
            _loading.value = false
        }
    }
}
