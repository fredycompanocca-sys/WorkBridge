package com.tecsup.workbridge.data.repository

import android.content.Context
import android.net.Uri
import com.tecsup.workbridge.data.local.dao.TrabajoDao
import com.tecsup.workbridge.data.local.entity.TrabajoEntity
import com.tecsup.workbridge.data.remote.api.WorkBridgeApiService
import com.tecsup.workbridge.data.remote.firebase.FirestoreService
import com.tecsup.workbridge.data.remote.firebase.FirebaseStorageService
import com.tecsup.workbridge.domain.model.Categoria
import com.tecsup.workbridge.domain.model.Trabajo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Repositorio central de Trabajos (Patrón Repository).
 * Actúa como Fuente Única de la Verdad (SSOT) orquestando:
 * 1. Cloud Firestore (Remoto en tiempo real)
 * 2. Room Database (Caché local y favoritos offline)
 * 3. Firebase Storage / Base64 (Gestión de imágenes)
 * 4. Retrofit REST API (Catálogo de categorías externas)
 */
class TrabajoRepository(
    private val context: Context,
    private val firestoreService: FirestoreService,
    private val storageService: FirebaseStorageService,
    private val trabajoDao: TrabajoDao,
    private val apiService: WorkBridgeApiService
) {

    /**
     * Obtiene el listado de trabajos combinando los datos en tiempo real de Firestore
     * con los favoritos marcados localmente en Room.
     */
    fun obtenerTrabajos(): Flow<List<Trabajo>> {
        val remotosFlow = firestoreService.observarTrabajos()
        val favoritosFlow = trabajoDao.getFavoritos().map { entities ->
            entities.map { it.id }.toSet()
        }

        return remotosFlow.combine(favoritosFlow) { trabajos, favIds ->
            trabajos.map { trabajo ->
                trabajo.copy(esFavorito = favIds.contains(trabajo.id))
            }
        }
    }

    /**
     * Obtiene exclusivamente los microtrabajos guardados como favoritos en Room (Offline-First).
     */
    fun obtenerFavoritos(): Flow<List<Trabajo>> {
        return trabajoDao.getFavoritos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Alterna el estado de favorito de un trabajo en la base de datos local Room.
     */
    suspend fun alternarFavorito(trabajo: Trabajo) {
        val nuevoEstado = !trabajo.esFavorito
        val entidad = TrabajoEntity.fromDomain(trabajo, esFavorito = nuevoEstado)
        trabajoDao.insertTrabajo(entidad)
        trabajoDao.updateFavorito(trabajo.id, nuevoEstado)
    }

    /**
     * Publica un nuevo trabajo. Si incluye una foto tomada con la cámara,
     * primero la sube a Firebase Storage y luego persiste la entidad en Firestore.
     */
    suspend fun publicarTrabajo(trabajo: Trabajo, imagenUri: Uri?): Result<String> {
        var urlFinalImagen: String? = null

        if (imagenUri != null) {
            val uploadResult = storageService.procesarImagen(context, imagenUri)
            if (uploadResult.isSuccess) {
                urlFinalImagen = uploadResult.getOrNull()
            }
        }

        val trabajoConImagen = trabajo.copy(imagenUrl = urlFinalImagen)
        val resultadoFirestore = firestoreService.publicarTrabajo(trabajoConImagen)

        if (resultadoFirestore.isSuccess) {
            val nuevoId = resultadoFirestore.getOrNull().orEmpty()
            // Guarda una copia local en Room para visualización offline
            trabajoDao.insertTrabajo(
                TrabajoEntity.fromDomain(trabajoConImagen.copy(id = nuevoId))
            )
        }

        return resultadoFirestore
    }

    /**
     * Obtiene un trabajo específico por su identificador.
     */
    suspend fun obtenerTrabajoPorId(id: String): Result<Trabajo?> {
        // Intenta obtener desde Firestore
        val remotoResult = firestoreService.obtenerTrabajoPorId(id)
        if (remotoResult.isSuccess && remotoResult.getOrNull() != null) {
            val trabajo = remotoResult.getOrNull()!!
            val esFav = trabajoDao.getTrabajoById(id)?.esFavorito ?: false
            return Result.success(trabajo.copy(esFavorito = esFav))
        }

        // Si falla o no hay conexión, busca en Room (fallback)
        val localEntity = trabajoDao.getTrabajoById(id)
        return Result.success(localEntity?.toDomain())
    }

    /**
     * Consume las categorías mediante Retrofit (API REST).
     * Si no hay conexión o falla el endpoint, provee un catálogo por defecto para que la app no colapse.
     */
    suspend fun obtenerCategorias(): Result<List<Categoria>> {
        return try {
            val response = apiService.getCategorias()
            if (response.isSuccessful && response.body() != null) {
                val categorias = response.body()!!.map { it.toDomain() }
                Result.success(categorias)
            } else {
                Result.success(obtenerCategoriasLocalesFallback())
            }
        } catch (e: Exception) {
            // Manejo de resiliencia: retorno de catálogo local por defecto
            Result.success(obtenerCategoriasLocalesFallback())
        }
    }

    private fun obtenerCategoriasLocalesFallback(): List<Categoria> {
        return listOf(
            Categoria("1", "Tutorías Académicas", "school", "Apoyo en matemáticas, física y programación"),
            Categoria("2", "Soporte TI y Redes", "computer", "Formateo, mantenimiento e instalación de software"),
            Categoria("3", "Diseño & Multimedia", "palette", "Creación de logos, diapositivas y edición de video"),
            Categoria("4", "Redacción & Monografías", "edit_document", "Revisión de normas APA, ensayos e informes"),
            Categoria("5", "Logística y Eventos", "event", "Apoyo en actividades dentro y fuera del campus")
        )
    }
}
