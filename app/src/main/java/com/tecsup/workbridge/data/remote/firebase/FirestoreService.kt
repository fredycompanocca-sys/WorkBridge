package com.tecsup.workbridge.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tecsup.workbridge.domain.model.Trabajo
import com.tecsup.workbridge.domain.model.Usuario
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Servicio para operaciones CRUD en tiempo real con Cloud Firestore.
 * Maneja las colecciones "trabajos" y "usuarios".
 */
class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val trabajosCollection = firestore.collection("trabajos")
    private val usuariosCollection = firestore.collection("usuarios")

    // ==================== USUARIOS ====================

    /**
     * Guarda o actualiza el perfil extendido del estudiante en Firestore.
     */
    suspend fun guardarPerfilUsuario(usuario: Usuario): Result<Unit> {
        return try {
            usuariosCollection.document(usuario.id).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     */
    suspend fun obtenerPerfilUsuario(userId: String): Result<Usuario?> {
        return try {
            val snapshot = usuariosCollection.document(userId).get().await()
            val usuario = snapshot.toObject(Usuario::class.java)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== TRABAJOS ====================

    /**
     * Publica un nuevo microtrabajo en Firestore.
     */
    suspend fun publicarTrabajo(trabajo: Trabajo): Result<String> {
        return try {
            val docRef = if (trabajo.id.isBlank()) trabajosCollection.document() else trabajosCollection.document(trabajo.id)
            val trabajoConId = trabajo.copy(id = docRef.id)
            docRef.set(trabajoConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Observa la lista de trabajos en tiempo real mediante un Flow de Kotlin.
     */
    fun observarTrabajos(): Flow<List<Trabajo>> = callbackFlow {
        val listener = trabajosCollection
            .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val trabajos = snapshot.toObjects(Trabajo::class.java)
                    trySend(trabajos)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Obtiene un trabajo específico por su ID.
     */
    suspend fun obtenerTrabajoPorId(trabajoId: String): Result<Trabajo?> {
        return try {
            val snapshot = trabajosCollection.document(trabajoId).get().await()
            val trabajo = snapshot.toObject(Trabajo::class.java)
            Result.success(trabajo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un trabajo por su ID.
     */
    suspend fun eliminarTrabajo(trabajoId: String): Result<Unit> {
        return try {
            trabajosCollection.document(trabajoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
