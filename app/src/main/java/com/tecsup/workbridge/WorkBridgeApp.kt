package com.tecsup.workbridge

import android.app.Application
import com.tecsup.workbridge.data.local.AppDatabase
import com.tecsup.workbridge.data.remote.api.RetrofitClient
import com.tecsup.workbridge.data.remote.firebase.FirebaseAuthService
import com.tecsup.workbridge.data.remote.firebase.FirestoreService
import com.tecsup.workbridge.data.remote.firebase.FirebaseStorageService
import com.tecsup.workbridge.data.repository.AuthRepository
import com.tecsup.workbridge.data.repository.TrabajoRepository

/**
 * Clase Application principal de WorkBridge.
 * Implementa un contenedor de inyección de dependencias manual (Service Locator Pattern),
 * ideal para proyectos académicos: altamente desacoplado, sin la complejidad de Dagger/Hilt
 * y fácil de defender ante el jurado evaluador.
 */
class WorkBridgeApp : Application() {

    // Capa de persistencia local (Room)
    val database by lazy { AppDatabase.getDatabase(this) }

    // Capa remota: Firebase & Retrofit
    val authService by lazy { FirebaseAuthService() }
    val firestoreService by lazy { FirestoreService() }
    val storageService by lazy { FirebaseStorageService() }
    val apiService by lazy { RetrofitClient.apiService }

    // Repositorios (Single Source of Truth)
    val authRepository by lazy {
        AuthRepository(authService, firestoreService)
    }

    val trabajoRepository by lazy {
        TrabajoRepository(
            context = this,
            firestoreService = firestoreService,
            storageService = storageService,
            trabajoDao = database.trabajoDao(),
            apiService = apiService
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}
