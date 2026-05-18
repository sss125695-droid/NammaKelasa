package com.example.nammakelasa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakelasa.model.Booking
import com.example.nammakelasa.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(private val repository: BookingRepository = BookingRepository()) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun fetchBookings(userId: String, isWorker: Boolean) {
        viewModelScope.launch {
            repository.getBookingsForUser(userId, isWorker).collect {
                _bookings.value = it
            }
        }
    }

    fun createBooking(booking: Booking, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            repository.createBooking(booking).onSuccess {
                onSuccess()
            }
            _loading.value = false
        }
    }

    fun updateStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
        }
    }
}
