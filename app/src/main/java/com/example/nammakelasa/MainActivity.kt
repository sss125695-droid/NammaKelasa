package com.example.nammakelasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.nammakelasa.navigation.AppNavigation
import com.example.nammakelasa.ui.theme.NammaKelasaTheme
import com.example.nammakelasa.viewmodel.*

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val workerViewModel: WorkerViewModel by viewModels()
    private val jobViewModel: JobViewModel by viewModels()
    private val bookingViewModel: BookingViewModel by viewModels()
    private val reviewViewModel: ReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            NammaKelasaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        workerViewModel = workerViewModel,
                        jobViewModel = jobViewModel,
                        bookingViewModel = bookingViewModel,
                        reviewViewModel = reviewViewModel
                    )
                }
            }
        }
    }
}
