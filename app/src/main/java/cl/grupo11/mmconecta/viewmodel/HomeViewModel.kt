package cl.grupo11.mmconecta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.grupo11.mmconecta.repository.FavoritesRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import cl.grupo11.mmconecta.repository.ContentRepository
import cl.grupo11.mmconecta.repository.DemoContentRepository
import cl.grupo11.mmconecta.model.LearningContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Todos",
    val contents: List<LearningContent> = emptyList(),
    val contentsLoading: Boolean = true,
    val favoriteIds: Set<String> = emptySet(),
    val onlyFavorites: Boolean = false,
    val favoritesBusy: Boolean = true,
    val favoritesLoaded: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val favorites: FavoritesRepository,
    private val repository: ContentRepository = DemoContentRepository()
) : ViewModel() {
    private var allContents: List<LearningContent> = emptyList()
    private val mutableState = MutableStateFlow(HomeUiState())
    val uiState = mutableState.asStateFlow()

    fun findContent(id: String?): LearningContent? = allContents.find { it.id == id }

    init {
        // El catalogo se carga primero (puede venir de red via Retrofit) y
        // recien despues los favoritos, para que la interseccion de ids se
        // calcule contra la lista real y no contra una lista vacia.
        viewModelScope.launch {
            loadContentsInternal()
            loadFavoritesInternal()
        }
    }

    private fun filtered(state: HomeUiState): HomeUiState = state.copy(contents = allContents.filter {
        (state.selectedCategory == "Todos" || it.category == state.selectedCategory) &&
            (!state.onlyFavorites || it.id in state.favoriteIds)
    })

    private suspend fun loadContentsInternal() {
        mutableState.update { it.copy(contentsLoading = true, error = null) }
        try {
            allContents = repository.getContents()
            mutableState.update {
                filtered(
                    it.copy(
                        categories = listOf("Todos") + allContents.map { content -> content.category }.distinct(),
                        contentsLoading = false
                    )
                )
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            mutableState.update {
                it.copy(contentsLoading = false, error = "No pudimos cargar el catálogo. Revisa tu conexión e inténtalo otra vez.")
            }
        }
    }

    fun retryContents() {
        viewModelScope.launch { loadContentsInternal() }
    }

    fun selectCategory(category: String) {
        if (category !in uiState.value.categories) return
        mutableState.update { filtered(it.copy(selectedCategory = category)) }
    }

    fun showFavorites(onlyFavorites: Boolean) {
        mutableState.update { filtered(it.copy(onlyFavorites = onlyFavorites)) }
    }

    private suspend fun loadFavoritesInternal() {
        // No limpiamos "error" aquí: si la carga del catálogo falló justo antes,
        // ese mensaje debe seguir visible aunque los favoritos carguen bien.
        mutableState.update { it.copy(favoritesBusy = true) }
        try {
            val ids = favorites.load().intersect(allContents.map { it.id }.toSet())
            mutableState.update { filtered(it.copy(favoriteIds = ids, favoritesBusy = false, favoritesLoaded = true)) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            mutableState.update { it.copy(favoritesBusy = false, error = "No pudimos cargar tus favoritos. Inténtalo otra vez.") }
        }
    }

    fun loadFavorites() {
        if (uiState.value.favoritesLoaded) return
        viewModelScope.launch { loadFavoritesInternal() }
    }

    fun toggleFavorite(id: String) {
        val state = uiState.value
        if (state.favoritesBusy || !state.favoritesLoaded || findContent(id) == null) return
        val ids = if (id in state.favoriteIds) state.favoriteIds - id else state.favoriteIds + id
        mutableState.update { it.copy(favoritesBusy = true, error = null) }
        viewModelScope.launch {
            try {
                favorites.save(ids)
                mutableState.update { filtered(it.copy(favoriteIds = ids, favoritesBusy = false)) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                mutableState.update { it.copy(favoritesBusy = false, error = "No pudimos cambiar el favorito. Vuelve a pulsar el botón para reintentar.") }
            }
        }
    }
}
