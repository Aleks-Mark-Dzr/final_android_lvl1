package com.example.skillcinema.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Collection
import com.example.skillcinema.data.HistoryItem
import com.example.skillcinema.data.ItemType
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.database.MovieEntity
import com.example.skillcinema.data.repository.MovieDetailRepository
import com.example.skillcinema.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val movieDetailRepository: MovieDetailRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val customCollections = MutableStateFlow<List<Collection>>(emptyList())
    private var _favoriteMoviesList: List<Movie> = emptyList()
    private var _watchLaterMoviesList: List<Movie> = emptyList()
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
        loadCustomCollections()
        loadHistory()
        observeFavoriteMovies()
        observeWatchedMovies()
        observeWatchLaterMovies()
        observeCustomCollectionMovies()
    }

    private fun loadCustomCollections() {
        val saved = profileRepository.loadCustomCollections()
        if (saved.isNotEmpty()) {
            customCollections.value = saved
            nextCustomCollectionId = (saved.maxOfOrNull { it.id } ?: WATCH_LATER_COLLECTION_ID) + 1
            updateCollections()
        }
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

    private fun observeWatchLaterMovies() {
        viewModelScope.launch {
            movieDetailRepository.getWatchLaterMovies().collect { watchLater ->
                _watchLaterMoviesList = watchLater
                updateCollections()
            }
        }
    }

    private fun observeCustomCollectionMovies() {
        viewModelScope.launch {
            movieDetailRepository.getAllMovieEntities().collect { entities ->
                val moviesByCollectionId = mutableMapOf<Int, MutableList<Movie>>()
                entities.forEach { entity ->
                    val movie = entity.toMovie()
                    parseCollectionIds(entity.inCollections).forEach { id ->
                        moviesByCollectionId.getOrPut(id) { mutableListOf() }.add(movie)
                    }
                }
                customCollections.value = customCollections.value.map { collection ->
                    collection.copy(items = moviesByCollectionId[collection.id].orEmpty())
                }
                updateCollections()
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _history.value = emptyList()
        }
    }
    fun addCustomCollection(name: String): Int? {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return null
        val newCollection = Collection(nextCustomCollectionId++, trimmedName, emptyList())
        customCollections.value = customCollections.value + newCollection
        updateCollections()
        persistCustomCollections()
        return newCollection.id
    }

    fun renameCustomCollection(collectionId: Int, newName: String) {
        val trimmedName = newName.trim()
        if (trimmedName.isEmpty()) return
        customCollections.value = customCollections.value.map { collection ->
            if (collection.id == collectionId) {
                collection.copy(name = trimmedName)
            } else {
                collection
            }
        }
        updateCollections()
        persistCustomCollections()
    }

    fun deleteCustomCollection(collectionId: Int) {
        customCollections.value = customCollections.value.filterNot { it.id == collectionId }
        updateCollections()
        persistCustomCollections()
    }

    private fun updateCollections() {
        _collections.value = listOf(
            Collection(FAVORITES_COLLECTION_ID, "Любимые", _favoriteMoviesList),
            Collection(WATCH_LATER_COLLECTION_ID, "Хочу посмотреть", _watchLaterMoviesList)
        ) + customCollections.value
    }

    private fun persistCustomCollections() {
        profileRepository.saveCustomCollections(customCollections.value)
    }

    private fun parseCollectionIds(value: String): Set<Int> {
        if (value.isBlank()) return emptySet()
        return value.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    private fun MovieEntity.toMovie(): Movie {
        return Movie(
            filmId = movieId,
            kinopoiskId = movieId,
            nameRu = nameRu,
            year = year.orEmpty(),
            posterUrlPreview = posterUrl.orEmpty(),
            rating = rating,
            ratingKinopoisk = rating,
            genres = emptyList()
        )
    }


    companion object {
        private const val FAVORITES_COLLECTION_ID = 1
        private const val WATCH_LATER_COLLECTION_ID = 2
    }
}