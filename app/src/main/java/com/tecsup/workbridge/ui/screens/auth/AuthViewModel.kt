package com.tecsup.workbridge.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.workbridge.data.repository.AuthRepository
import com.tecsup.workbridge.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val estaCargando: Boolean = false,
    val error: String? = null,
    val usuario: Usuario? = null,
    val operacionExitosa: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun iniciarSesion(email: String, contrasenia: String, onExito: () -> Unit) {
        if (email.isBlank() || contrasenia.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, error = null) }
            val resultado = authRepository.iniciarSesion(email.trim(), contrasenia.trim())
            if (resultado.isSuccess) {
                _uiState.update {
                    it.copy(estaCargando = false, usuario = resultado.getOrNull(), operacionExitosa = true)
                }
                onExito()
            } else {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        error = resultado.exceptionOrNull()?.localizedMessage ?: "Credenciales incorrectas"
                    )
                }
            }
        }
    }

    fun registrarse(
        nombre: String,
        email: String,
        contrasenia: String,
        carrera: String,
        telefono: String,
        onExito: () -> Unit
    ) {
        if (nombre.isBlank() || email.isBlank() || contrasenia.isBlank() || carrera.isBlank()) {
            _uiState.update { it.copy(error = "Por favor llena los campos requeridos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, error = null) }
            val resultado = authRepository.registrarUsuario(
                nombre = nombre.trim(),
                email = email.trim(),
                contrasenia = contrasenia.trim(),
                carrera = carrera.trim(),
                telefono = telefono.trim()
            )
            if (resultado.isSuccess) {
                _uiState.update {
                    it.copy(estaCargando = false, usuario = resultado.getOrNull(), operacionExitosa = true)
                }
                onExito()
            } else {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        error = resultado.exceptionOrNull()?.localizedMessage ?: "Error al registrarse"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(error = null) }
    }
}
