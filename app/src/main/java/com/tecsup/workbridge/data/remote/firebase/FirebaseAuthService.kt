package com.tecsup.workbridge.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Servicio encargado de la comunicación con Firebase Authentication.
 * Gestiona el registro, inicio de sesión y sesión activa del usuario.
 */
class FirebaseAuthService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    val currentUserEmail: String
        get() = auth.currentUser?.email.orEmpty()

    /**
     * Inicia sesión con Correo Electrónico y Contraseña.
     */
    suspend fun login(email: String, contrasenia: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, contrasenia).await()
            val user = authResult.user ?: throw Exception("Usuario no encontrado tras login.")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario en Firebase Auth.
     */
    suspend fun register(email: String, contrasenia: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, contrasenia).await()
            val user = authResult.user ?: throw Exception("Error al crear cuenta de usuario.")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión activa.
     */
    fun logout() {
        auth.signOut()
    }
}
