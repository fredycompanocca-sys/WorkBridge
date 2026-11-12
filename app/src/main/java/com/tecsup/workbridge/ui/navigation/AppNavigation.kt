package com.tecsup.workbridge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.workbridge.data.repository.AuthRepository
import com.tecsup.workbridge.data.repository.TrabajoRepository
import com.tecsup.workbridge.ui.screens.auth.AuthViewModel
import com.tecsup.workbridge.ui.screens.auth.LoginScreen
import com.tecsup.workbridge.ui.screens.auth.RegisterScreen
import com.tecsup.workbridge.ui.screens.create.CreateJobScreen
import com.tecsup.workbridge.ui.screens.create.CreateJobViewModel
import com.tecsup.workbridge.ui.screens.detail.DetailScreen
import com.tecsup.workbridge.ui.screens.detail.DetailViewModel
import com.tecsup.workbridge.ui.screens.home.HomeScreen
import com.tecsup.workbridge.ui.screens.home.HomeViewModel
import com.tecsup.workbridge.ui.screens.profile.ProfileScreen
import com.tecsup.workbridge.ui.screens.profile.ProfileViewModel

@Composable
fun AppNavigation(
    authRepository: AuthRepository,
    trabajoRepository: TrabajoRepository,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (authRepository.isUserLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = viewModel {
                AuthViewModel(authRepository)
            }
            LoginScreen(
                viewModel = viewModel,
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Register.route) {
            val viewModel: AuthViewModel = viewModel {
                AuthViewModel(authRepository)
            }
            RegisterScreen(
                viewModel = viewModel,
                onRegistroExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegresarALogin = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla Principal (Feed de Microtrabajos)
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel(trabajoRepository)
            }
            HomeScreen(
                viewModel = viewModel,
                onTrabajoClick = { trabajoId ->
                    navController.navigate(Screen.Detail.createRoute(trabajoId))
                },
                onCrearTrabajoClick = {
                    navController.navigate(Screen.Create.route)
                },
                onPerfilClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Detalle de un Microtrabajo
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("trabajoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val trabajoId = backStackEntry.arguments?.getString("trabajoId").orEmpty()
            val viewModel: DetailViewModel = viewModel {
                DetailViewModel(trabajoRepository, trabajoId)
            }
            DetailScreen(
                viewModel = viewModel,
                onRegresar = { navController.popBackStack() }
            )
        }

        // Crear / Publicar nuevo Microtrabajo
        composable(Screen.Create.route) {
            val viewModel: CreateJobViewModel = viewModel {
                CreateJobViewModel(trabajoRepository, authRepository)
            }
            CreateJobScreen(
                viewModel = viewModel,
                onPublicacionExitosa = {
                    navController.popBackStack()
                },
                onRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // Perfil del Estudiante
        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = viewModel {
                ProfileViewModel(authRepository)
            }
            ProfileScreen(
                viewModel = viewModel,
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegresar = {
                    navController.popBackStack()
                }
            )
        }
    }
}
