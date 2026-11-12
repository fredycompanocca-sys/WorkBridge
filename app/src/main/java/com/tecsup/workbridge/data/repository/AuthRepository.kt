package com.tecsup.workbridge.data.repository

import com.tecsup.workbridge.data.remote.firebase.FirebaseAuthService
import com.tecsup.workbridge.data.remote.firebase.FirestoreService
import com.tecsup.workbridge.domain.model.Usuario

/**
 * Repositorio de Autenticación y Gestión de Perfil de Usuario.
 * Conecta Firebase Authentication con el documento de usuario en Cloud Firestore.
 */
class AuthRepository(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) {

    val isUserLoggedIn: Boolean
        get() = authService.isUserLoggedIn

    val currentUserId: String
        get() = authService.currentUserId

    /**
     * Inicia sesión y carga el perfil desde Firestore.
     */
    suspend fun iniciarSesion(email: String, contrasenia: String): Result<Usuario?> {
        val authResult = authService.login(email, contrasenia)
        if (authResult.isFailure) {
            return Result.failure(authResult.exceptionOrNull() ?: Exception("Error al autenticar"))
        }

        val uid = authResult.getOrNull()?.uid ?: return Result.failure(Exception("UID no válido"))
        return firestoreService.obtenerPerfilUsuario(uid)
    }

    /**
     * Registra un nuevo estudiante en Firebase Auth y crea su documento en Firestore.
     */
    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        contrasenia: String,
        carrera: String,
        telefono: String
    ): Result<Usuario> {
        val authResult = authService.register(email, contrasenia)
        if (authResult.isFailure) {
            return Result.failure(authResult.exceptionOrNull() ?: Exception("Error al registrar"))
        }

        val uid = authResult.getOrNull()?.uid ?: return Result.failure(Exception("UID no generado"))
        val nuevoUsuario = Usuario(
            id = uid,
            nombre = nombre,
            email = email,
            carrera = carrera,
            telefono = telefono,
            fotoUrl = "",
            calificacionPromedio = 5.0
        )

        val firestoreResult = firestoreService.guardarPerfilUsuario(nuevoUsuario)
        return if (firestoreResult.isSuccess) {
            Result.success(nuevoUsuario)
        } else {
            Result.failure(firestoreResult.exceptionOrNull() ?: Exception("Error guardando perfil"))
        }
    }

    /**
     * Obtiene los datos del usuario actualmente autenticado.
     */
    suspend fun obtenerUsuarioActual(): Result<Usuario?> {
        val uid = authService.currentUserId
        if (uid.isBlank()) return Result.success(null)
        return firestoreService.obtenerPerfilUsuario(uid)
    }

    /**
     * Actualiza los datos del perfil en Firestore.
     */
    suspend fun actualizarPerfil(usuario: Usuario): Result<Unit> {
        return firestoreService.guardarPerfilUsuario(usuario)
    }

    /**
     * Cierra la sesión activa.
     */
    fun cerrarSesion() {
        authService.logout()
    }
}
