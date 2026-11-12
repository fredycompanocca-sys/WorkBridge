package com.tecsup.workbridge.domain.model

/**
 * Modelo de dominio puro para un Microtrabajo o Servicio Estudiantil.
 * Representa la entidad central del negocio en WorkBridge.
 */
data class Trabajo(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "General",
    val pago: Double = 0.0,
    val autorId: String = "",
    val autorNombre: String = "",
    val autorEmail: String = "",
    val ubicacionCampus: String = "Campus Principal TECSUP",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imagenUrl: String? = null,
    val estado: String = "DISPONIBLE", // DISPONIBLE, EN_PROCESO, FINALIZADO
    val fechaPublicacion: Long = System.currentTimeMillis(),
    val esFavorito: Boolean = false
)
