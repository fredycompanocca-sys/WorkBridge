package com.tecsup.workbridge.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.workbridge.data.repository.TrabajoRepository
import com.tecsup.workbridge.domain.model.Categoria
import com.tecsup.workbridge.domain.model.Trabajo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val trabajos: List<Trabajo> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val categoriaSeleccionada: String = "Todas",
    val busquedaTexto: String = "",
    val mostrarSoloFavoritos: Boolean = false,
    val estaCargando: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val trabajoRepository: TrabajoRepository
) : ViewModel() {

    private val _filtroCategoria = MutableStateFlow("Todas")
    private val _busqueda = MutableStateFlow("")
    private val _soloFavoritos = MutableStateFlow(false)
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    private val _cargando = MutableStateFlow(false)

    init {
        cargarCategorias()
    }

    // Combinación reactiva de flujos para filtrado instantáneo
    val uiState: StateFlow<HomeUiState> = combine(
        trabajoRepository.obtenerTrabajos(),
        _filtroCategoria,
        _busqueda,
        _soloFavoritos,
        _categorias
    ) { listaTrabajos, categoria, query, soloFavs, cats ->
        val filtrados = listaTrabajos.filter { t ->
            val coincideCategoria = (categoria == "Todas" || t.categoria.equals(categoria, ignoreCase = true))
            val coincideBusqueda = query.isBlank() ||
                    t.titulo.contains(query, ignoreCase = true) ||
                    t.descripcion.contains(query, ignoreCase = true)
            val coincideFavorito = !soloFavs || t.esFavorito

            coincideCategoria && coincideBusqueda && coincideFavorito
        }

        HomeUiState(
            trabajos = filtrados,
            categorias = cats,
            categoriaSeleccionada = categoria,
            busquedaTexto = query,
            mostrarSoloFavoritos = soloFavs,
            estaCargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(estaCargando = true)
    )

    private fun cargarCategorias() {
        viewModelScope.launch {
            val resultado = trabajoRepository.obtenerCategorias()
            if (resultado.isSuccess) {
                _categorias.value = resultado.getOrDefault(emptyList())
            }
        }
    }

    fun onBusquedaCambio(nuevaBusqueda: String) {
        _busqueda.value = nuevaBusqueda
    }

    fun onCategoriaSeleccionada(categoria: String) {
        _filtroCategoria.value = categoria
    }

    fun onAlternarSoloFavoritos() {
        _soloFavoritos.update { !it }
    }

    fun alternarFavorito(trabajo: Trabajo) {
        viewModelScope.launch {
            trabajoRepository.alternarFavorito(trabajo)
        }
    }
}
