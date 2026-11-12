package com.tecsup.workbridge.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.workbridge.data.repository.AuthRepository
import com.tecsup.workbridge.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val usuario: Usuario? = null,
    val estaCargando: Boolean = false,
    val mensajeExito: String? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(estaCargando = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, error = null) }
            val resultado = authRepository.obtenerUsuarioActual()
            if (resultado.isSuccess) {
                _uiState.update {
                    it.copy(estaCargando = false, usuario = resultado.getOrNull())
                }
            } else {
                _uiState.update {
                    it.copy(estaCargando = false, error = "No se pudo cargar el perfil")
                }
            }
        }
    }

    fun actualizarPerfil(carrera: String, telefono: String) {
        val actual = _uiState.value.usuario ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            val actualizado = actual.copy(carrera = carrera, telefono = telefono)
            val resultado = authRepository.actualizarPerfil(actualizado)
            if (resultado.isSuccess) {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        usuario = actualizado,
                        mensajeExito = "Perfil actualizado con éxito"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(estaCargando = false, error = "Error al actualizar")
                }
            }
        }
    }

    fun cerrarSesion(onCerrarSesion: () -> Unit) {
        authRepository.cerrarSesion()
        onCerrarSesion()
    }
}
