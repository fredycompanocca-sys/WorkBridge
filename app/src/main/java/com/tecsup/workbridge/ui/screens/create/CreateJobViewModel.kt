package com.tecsup.workbridge.ui.screens.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.workbridge.data.repository.AuthRepository
import com.tecsup.workbridge.data.repository.TrabajoRepository
import com.tecsup.workbridge.domain.model.Categoria
import com.tecsup.workbridge.domain.model.Trabajo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateJobUiState(
    val categorias: List<Categoria> = emptyList(),
    val estaGuardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val error: String? = null
)

class CreateJobViewModel(
    private val trabajoRepository: TrabajoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateJobUiState())
    val uiState: StateFlow<CreateJobUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            val resultado = trabajoRepository.obtenerCategorias()
            if (resultado.isSuccess) {
                _uiState.update { it.copy(categorias = resultado.getOrDefault(emptyList())) }
            }
        }
    }

    fun publicarTrabajo(
        titulo: String,
        descripcion: String,
        categoria: String,
        pagoStr: String,
        campus: String,
        latitud: Double?,
        longitud: Double?,
        imagenUri: Uri?,
        onExito: () -> Unit
    ) {
        if (titulo.isBlank() || descripcion.isBlank() || categoria.isBlank() || pagoStr.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos obligatorios") }
            return
        }

        val pago = pagoStr.toDoubleOrNull()
        if (pago == null || pago <= 0) {
            _uiState.update { it.copy(error = "El pago debe ser un monto numérico positivo") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(estaGuardando = true, error = null) }

            val usuarioActual = authRepository.obtenerUsuarioActual().getOrNull()

            val nuevoTrabajo = Trabajo(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria,
                pago = pago,
                autorId = usuarioActual?.id ?: authRepository.currentUserId,
                autorNombre = usuarioActual?.nombre ?: "Estudiante TECSUP",
                autorEmail = usuarioActual?.email ?: "",
                ubicacionCampus = campus.ifBlank { "Campus Principal TECSUP" },
                latitud = latitud,
                longitud = longitud,
                fechaPublicacion = System.currentTimeMillis()
            )

            val resultado = trabajoRepository.publicarTrabajo(nuevoTrabajo, imagenUri)

            if (resultado.isSuccess) {
                _uiState.update { it.copy(estaGuardando = false, guardadoExitoso = true) }
                onExito()
            } else {
                _uiState.update {
                    it.copy(
                        estaGuardando = false,
                        error = resultado.exceptionOrNull()?.localizedMessage ?: "Error al publicar trabajo"
                    )
                }
            }
        }
    }
}
