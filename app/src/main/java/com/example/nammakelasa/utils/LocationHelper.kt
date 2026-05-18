package com.example.nammakelasa.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationHelper(private val context: Context) {
    private val TAG = "LocationHelper"
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!isLocationEnabled()) {
            Log.w(TAG, "Location services are disabled on the device.")
            return null
        }

        return try {
            Log.d(TAG, "Attempting to get current location...")
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
            
            if (location == null) {
                Log.w(TAG, "FusedLocationProvider returned null location.")
                // Try last known location as fallback
                val lastKnown = fusedLocationClient.lastLocation.await()
                if (lastKnown != null) {
                    Log.d(TAG, "Using last known location as fallback.")
                    return lastKnown
                }
            }
            location
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current location: ${e.message}", e)
            null
        }
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        if (latitude == 0.0 && longitude == 0.0) return ""
        
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            // Geocoder.getFromLocation is blocking, but in this app it's called from a coroutine scope in the UI
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val subLocality = address.subLocality ?: ""
                val locality = address.locality ?: ""
                val adminArea = address.adminArea ?: ""
                
                when {
                    subLocality.isNotEmpty() && locality.isNotEmpty() -> "$subLocality, $locality"
                    locality.isNotEmpty() -> locality
                    else -> adminArea.ifEmpty { "Lat: $latitude, Lon: $longitude" }
                }
            } else {
                "Lat: $latitude, Lon: $longitude"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder error: ${e.message}")
            "Lat: $latitude, Lon: $longitude"
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // returns in km
    }
}
