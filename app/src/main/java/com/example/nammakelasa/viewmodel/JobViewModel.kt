package com.example.nammakelasa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakelasa.model.Job
import com.example.nammakelasa.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobViewModel(private val repository: JobRepository = JobRepository()) : ViewModel() {

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        fetchJobs()
    }

    private fun fetchJobs() {
        viewModelScope.launch {
            repository.getAllJobs().collect {
                _jobs.value = it
            }
        }
    }

    fun postJob(job: Job, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            repository.postJob(job).onSuccess {
                onSuccess()
            }
            _loading.value = false
        }
    }
}
