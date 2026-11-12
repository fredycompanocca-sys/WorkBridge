package com.tecsup.workbridge.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.workbridge.data.repository.TrabajoRepository
import com.tecsup.workbridge.domain.model.Trabajo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val trabajo: Trabajo? = null,
    val estaCargando: Boolean = false,
    val postuladoExitosamente: Boolean = false,
    val error: String? = null
)

class DetailViewModel(
    private val trabajoRepository: TrabajoRepository,
    private val trabajoId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(estaCargando = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    private fun cargarDetalle() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, error = null) }
            val resultado = trabajoRepository.obtenerTrabajoPorId(trabajoId)
            if (resultado.isSuccess) {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        trabajo = resultado.getOrNull()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        error = "No se pudo cargar el detalle del trabajo"
                    )
                }
            }
        }
    }

    fun alternarFavorito() {
        val actual = _uiState.value.trabajo ?: return
        viewModelScope.launch {
            trabajoRepository.alternarFavorito(actual)
            _uiState.update {
                it.copy(trabajo = actual.copy(esFavorito = !actual.esFavorito))
            }
        }
    }

    fun postularAlTrabajo() {
        _uiState.update { it.copy(postuladoExitosamente = true) }
    }
}
