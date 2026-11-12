package com.tecsup.workbridge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tecsup.workbridge.domain.model.Trabajo

/**
 * Entidad de Room para la persistencia local de trabajos (Caché offline y favoritos).
 */
@Entity(tableName = "trabajos")
data class TrabajoEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val pago: Double,
    val autorId: String,
    val autorNombre: String,
    val autorEmail: String,
    val ubicacionCampus: String,
    val latitud: Double?,
    val longitud: Double?,
    val imagenUrl: String?,
    val estado: String,
    val fechaPublicacion: Long,
    val esFavorito: Boolean
) {
    /**
     * Mapeador de Entidad Local a Modelo de Dominio
     */
    fun toDomain(): Trabajo {
        return Trabajo(
            id = id,
            titulo = titulo,
            descripcion = descripcion,
            categoria = categoria,
            pago = pago,
            autorId = autorId,
            autorNombre = autorNombre,
            autorEmail = autorEmail,
            ubicacionCampus = ubicacionCampus,
            latitud = latitud,
            longitud = longitud,
            imagenUrl = imagenUrl,
            estado = estado,
            fechaPublicacion = fechaPublicacion,
            esFavorito = esFavorito
        )
    }

    companion object {
        /**
         * Mapeador de Modelo de Dominio a Entidad Local
         */
        fun fromDomain(domain: Trabajo, esFavorito: Boolean = domain.esFavorito): TrabajoEntity {
            return TrabajoEntity(
                id = domain.id,
                titulo = domain.titulo,
                descripcion = domain.descripcion,
                categoria = domain.categoria,
                pago = domain.pago,
                autorId = domain.autorId,
                autorNombre = domain.autorNombre,
                autorEmail = domain.autorEmail,
                ubicacionCampus = domain.ubicacionCampus,
                latitud = domain.latitud,
                longitud = domain.longitud,
                imagenUrl = domain.imagenUrl,
                estado = domain.estado,
                fechaPublicacion = domain.fechaPublicacion,
                esFavorito = esFavorito
            )
        }
    }
}
