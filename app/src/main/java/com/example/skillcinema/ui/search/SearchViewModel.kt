package com.example.skillcinema.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.skillcinema.data.repository.MovieRepository
//import com.example.skillcinema.data.model.Movie

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> get() = _searchResults.asStateFlow()

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                Log.d("SearchViewModel", "Searching movies for: $query") // Логируем запрос
                val results = repository.searchMovies(query)
                _searchResults.value = results
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching movies: ${e.message}", e)
                _searchResults.value = emptyList()
            }
        }
    }
}