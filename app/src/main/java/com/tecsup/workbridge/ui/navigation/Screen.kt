package com.tecsup.workbridge.ui.navigation

/**
 * Rutas selladas para navegación tipada y segura en Jetpack Compose.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Create : Screen("create")
    object Profile : Screen("profile")
    
    object Detail : Screen("detail/{trabajoId}") {
        fun createRoute(trabajoId: String) = "detail/$trabajoId"
    }
}
