package com.example.nammakelasa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nammakelasa.ui.screens.*
import com.example.nammakelasa.viewmodel.*

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    workerViewModel: WorkerViewModel = viewModel(),
    jobViewModel: JobViewModel = viewModel(),
    bookingViewModel: BookingViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onWorkerLogin = { navController.navigate(Screen.WorkerLogin.route) },
                onCustomerBrowse = { navController.navigate(Screen.CustomerHome.route) }
            )
        }
        composable(Screen.WorkerLogin.route) {
            WorkerLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.WorkerDashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.WorkerRegistration.route) }
            )
        }
        composable(Screen.WorkerRegistration.route) {
            WorkerRegistrationScreen(
                authViewModel = authViewModel,
                workerViewModel = workerViewModel,
                onRegistrationSuccess = {
                    navController.navigate(Screen.WorkerDashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.WorkerDashboard.route) {
            WorkerDashboard(
                workerViewModel = workerViewModel,
                authViewModel = authViewModel,
                onUploadGallery = { navController.navigate(Screen.UploadWorkGallery.route) },
                onEditProfile = { navController.navigate(Screen.ProfileEdit.route) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.WorkerDashboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                viewModel = workerViewModel,
                onWorkerClick = { workerId ->
                    navController.navigate(Screen.WorkerDetail.createRoute(workerId))
                }
            )
        }
        composable(
            route = Screen.WorkerDetail.route,
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
            WorkerDetailScreen(
                workerId = workerId,
                viewModel = workerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.UploadWorkGallery.route) {
            UploadGalleryScreen(
                viewModel = workerViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(
                viewModel = workerViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PostJob.route) {
            PostJobScreen(
                jobViewModel = jobViewModel,
                authViewModel = authViewModel,
                onJobPosted = { navController.navigate(Screen.JobBrowse.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.JobBrowse.route) {
            JobBrowseScreen(
                viewModel = jobViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.MyBookings.route) {
            MyBookingsScreen(
                viewModel = bookingViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
