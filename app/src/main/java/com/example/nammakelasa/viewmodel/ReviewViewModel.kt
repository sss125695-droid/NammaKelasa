package com.example.nammakelasa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakelasa.model.Review
import com.example.nammakelasa.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository = ReviewRepository()) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun fetchReviews(workerId: String) {
        viewModelScope.launch {
            repository.getReviewsForWorker(workerId).collect {
                _reviews.value = it
            }
        }
    }

    fun addReview(review: Review, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            repository.addReview(review).onSuccess {
                onSuccess()
            }
            _loading.value = false
        }
    }
}
