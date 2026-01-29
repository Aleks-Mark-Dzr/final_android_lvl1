package com.example.skillcinema.ui.moviedetail.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.data.database.MovieEntity
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MovieCollectionsViewModel(
    private val repository: MovieDetailRepository,
    private val movieId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieCollectionsUiState())
    val uiState: StateFlow<MovieCollectionsUiState> = _uiState.asStateFlow()

    init {
        observeCollectionCounts()
        loadMovie()
    }

    private fun observeCollectionCounts() {
        viewModelScope.launch {
            combine(
                repository.getFavoriteMovies(),
                repository.getWatchLaterMovies(),
                repository.getAllMovieEntities()
            ) { favorites, watchLater, entities ->
                val counts = mutableMapOf<Int, Int>()
                counts[FAVORITES_COLLECTION_ID] = favorites.size
                counts[WATCH_LATER_COLLECTION_ID] = watchLater.size
                entities.forEach { entity ->
                    parseCollectionIds(entity.inCollections).forEach { id ->
                        counts[id] = (counts[id] ?: 0) + 1
                    }
                }
                counts
            }.collect { counts ->
                _uiState.value = _uiState.value.copy(collectionCounts = counts)
            }
        }
    }

    private fun loadMovie() {
        viewModelScope.launch {
            val movie = repository.getMovieDetails(movieId)
            if (movie != null) {
                repository.saveMovie(movie)
            }
            val cachedMovie = repository.getMovieById(movieId)
            val selected = buildSelectedCollections(cachedMovie)
            _uiState.value = _uiState.value.copy(movie = movie, selectedCollections = selected)
        }
    }

    fun toggleCollection(collectionId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val updated = currentState.selectedCollections.toMutableSet()
            if (isChecked) {
                updated.add(collectionId)
            } else {
                updated.remove(collectionId)
            }
            _uiState.value = currentState.copy(selectedCollections = updated)
            ensureMovieSaved(currentState.movie)
            when (collectionId) {
                FAVORITES_COLLECTION_ID -> repository.updateFavoriteStatus(movieId, isChecked)
                WATCH_LATER_COLLECTION_ID -> repository.updateWatchLaterStatus(movieId, isChecked)
                else -> {
                    val customIds = updated.filterNot { it == FAVORITES_COLLECTION_ID || it == WATCH_LATER_COLLECTION_ID }
                        .toSet()
                    repository.updateMovieCollections(movieId, customIds)
                }
            }
        }
    }

    fun selectCollection(collectionId: Int) {
        toggleCollection(collectionId, true)
    }

    private suspend fun ensureMovieSaved(movie: MovieDetailResponse?) {
        movie?.let { repository.saveMovie(it) }
    }

    private fun buildSelectedCollections(entity: MovieEntity?): Set<Int> {
        val selected = mutableSetOf<Int>()
        if (entity?.isFavorite == true) {
            selected.add(FAVORITES_COLLECTION_ID)
        }
        if (entity?.isWatchLater == true) {
            selected.add(WATCH_LATER_COLLECTION_ID)
        }
        selected.addAll(parseCollectionIds(entity?.inCollections ?: ""))
        return selected
    }

    private fun parseCollectionIds(value: String): Set<Int> {
        if (value.isBlank()) return emptySet()
        return value.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    companion object {
        const val FAVORITES_COLLECTION_ID = 1
        const val WATCH_LATER_COLLECTION_ID = 2
    }
}

data class MovieCollectionsUiState(
    val movie: MovieDetailResponse? = null,
    val selectedCollections: Set<Int> = emptySet(),
    val collectionCounts: Map<Int, Int> = emptyMap()
)