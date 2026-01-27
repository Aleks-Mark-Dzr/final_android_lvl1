package com.example.skillcinema.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Collection
import com.example.skillcinema.data.HistoryItem
import com.example.skillcinema.data.ItemType
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val movieDetailRepository: MovieDetailRepository
) : ViewModel() {

    private val customCollections = MutableStateFlow<List<Collection>>(emptyList())
    private var _favoriteMoviesList: List<Movie> = emptyList()
    private var nextCustomCollectionId = 3

    private val _collections = MutableStateFlow<List<Collection>>(
        listOf(
            Collection(FAVORITES_COLLECTION_ID, "Любимые", emptyList()),
            Collection(WATCH_LATER_COLLECTION_ID, "Хочу посмотреть", emptyList())
        )
    )
    val collections: StateFlow<List<Collection>> = _collections.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies.asStateFlow()

    private val _watchedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val watchedMovies: StateFlow<List<Movie>> = _watchedMovies.asStateFlow()

    init {
        loadHistory()
        observeFavoriteMovies()
        observeWatchedMovies()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _history.value = listOf(
                HistoryItem(1, "Кристофер Нолан", ItemType.PERSON),
                HistoryItem(2, "Интерстеллар", ItemType.MOVIE)
            )
        }
    }

    private fun observeFavoriteMovies() {
        viewModelScope.launch {
            movieDetailRepository.getFavoriteMovies().collect { favorites ->
                _favoriteMoviesList = favorites
                _favoriteMovies.value = favorites
                updateCollections()
            }
        }
    }

    private fun observeWatchedMovies() {
        viewModelScope.launch {
            movieDetailRepository.getWatchedMovies().collect { watched ->
                _watchedMovies.value = watched
            }
        }
    }

    private fun observeWatchedMovies() {
        viewModelScope.launch {
            movieDetailRepository.getWatchedMovies().collect { watched ->
                _watchedMovies.value = watched
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _history.value = emptyList()
        }
    }
    fun addCustomCollection(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return
        val newCollection = Collection(nextCustomCollectionId++, trimmedName, emptyList())
        customCollections.value = customCollections.value + newCollection
        updateCollections()
    }

    private fun updateCollections() {
        _collections.value = listOf(
            Collection(FAVORITES_COLLECTION_ID, "Любимые", _favoriteMoviesList),
            Collection(WATCH_LATER_COLLECTION_ID, "Хочу посмотреть", emptyList())
        ) + customCollections.value
    }

    companion object {
        private const val FAVORITES_COLLECTION_ID = 1
        private const val WATCH_LATER_COLLECTION_ID = 2
    }
}