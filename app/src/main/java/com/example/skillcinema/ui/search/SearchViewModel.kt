package com.example.skillcinema.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.network.Country
import com.example.skillcinema.network.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.skillcinema.data.repository.MovieRepository

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> get() = _searchResults.asStateFlow()
    private val _settings = MutableStateFlow(SearchSettings())
    val settings: StateFlow<SearchSettings> = _settings.asStateFlow()
    private val _availableCountries = MutableStateFlow<List<Country>>(emptyList())
    val availableCountries: StateFlow<List<Country>> = _availableCountries.asStateFlow()
    private val _availableGenres = MutableStateFlow<List<Genre>>(emptyList())
    val availableGenres: StateFlow<List<Genre>> = _availableGenres.asStateFlow()
    private var lastQuery: String = ""

    init {
        viewModelScope.launch {
            runCatching { repository.getAvailableGenresAndCountries() }
                .onSuccess {
                    _availableCountries.value = it.countries
                    _availableGenres.value = it.genres
                }
        }
    }

    fun searchMovies(query: String) {
        lastQuery = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                Log.d("SearchViewModel", "Searching movies for: $query") // Логируем запрос
                val results = repository.searchMovies(query, _settings.value)
                _searchResults.value = results
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching movies: ${e.message}", e)
                _searchResults.value = emptyList()
            }
        }
    }
    fun applySettings(newSettings: SearchSettings) {
        _settings.value = newSettings
        if (lastQuery.isNotBlank()) {
            searchMovies(lastQuery)
        }
    }
}