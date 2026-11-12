package com.tecsup.workbridge.domain.model

/**
 * Modelo de dominio puro para representar un Usuario (Estudiante/Empleador).
 * Desacoplado de frameworks y bibliotecas externas (Clean Architecture).
 */
data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val carrera: String = "",
    val telefono: String = "",
    val fotoUrl: String = "",
    val calificacionPromedio: Double = 5.0
)
