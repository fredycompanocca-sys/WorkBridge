package com.tecsup.workbridge.data.remote.api

import com.tecsup.workbridge.domain.model.Categoria
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz de Retrofit para interactuar con la API REST externa.
 * Cumple con el requisito académico de consumo de servicios web desacoplados.
 */
interface WorkBridgeApiService {

    /**
     * Obtiene el catálogo oficial de categorías y competencias académicas.
     * Endpoint de ejemplo que puede apuntar a MockAPI, Beeceptor o un backend propio.
     */
    @GET("api/v1/categorias")
    suspend fun getCategorias(): Response<List<CategoriaDto>>

    /**
     * Consulta una categoría específica por su identificador.
     */
    @GET("api/v1/categorias/{id}")
    suspend fun getCategoriaById(@Path("id") id: String): Response<CategoriaDto>
}

/**
 * Data Transfer Object (DTO) para deserialización con Gson desde la API REST.
 */
data class CategoriaDto(
    val id: String?,
    val nombre: String?,
    val icono: String?,
    val descripcion: String?
) {
    fun toDomain(): Categoria {
        return Categoria(
            id = id ?: "",
            nombre = nombre ?: "Sin categoría",
            icono = icono ?: "default_icon",
            descripcion = descripcion ?: ""
        )
    }
}
