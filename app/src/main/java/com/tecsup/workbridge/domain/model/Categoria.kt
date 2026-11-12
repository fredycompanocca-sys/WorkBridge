package com.tecsup.workbridge.domain.model

/**
 * Categoría de servicio o microtrabajo.
 * Puede ser consumida desde una API REST externa con Retrofit.
 */
data class Categoria(
    val id: String,
    val nombre: String,
    val icono: String,
    val descripcion: String
)
