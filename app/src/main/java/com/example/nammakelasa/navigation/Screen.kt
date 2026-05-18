package com.example.nammakelasa.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object WorkerRegistration : Screen("worker_registration")
    object WorkerLogin : Screen("worker_login")
    object WorkerDashboard : Screen("worker_dashboard")
    object UploadWorkGallery : Screen("upload_gallery")
    object CustomerHome : Screen("customer_home")
    object WorkerDetail : Screen("worker_detail/{workerId}") {
        fun createRoute(workerId: String) = "worker_detail/$workerId"
    }
    object ProfileEdit : Screen("profile_edit")
    object PostJob : Screen("post_job")
    object JobBrowse : Screen("job_browse")
    object MyBookings : Screen("my_bookings")
}
