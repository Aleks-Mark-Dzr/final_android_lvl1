package com.example.skillcinema.ui.homepage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomepageViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _premieres = MutableStateFlow<List<Movie>>(emptyList())
    val premieres: StateFlow<List<Movie>> get() = _premieres

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> get() = _popularMovies

    private val _dynamicCategory = MutableStateFlow<List<Movie>>(emptyList())
    val dynamicCategory: StateFlow<List<Movie>> get() = _dynamicCategory

    fun fetchPremieres(year: Int, month: String) {
        viewModelScope.launch {
            _premieres.value = repository.getPremieres(year, month) ?: emptyList()
        }
    }

    fun fetchPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            _popularMovies.value = repository.getTopMovies(page) ?: emptyList()
        }
    }

    fun fetchDynamicCategory(countryId: Int, genreId: Int) {
        viewModelScope.launch {
            _dynamicCategory.value = repository.getMoviesByGenreAndCountry(countryId, genreId) ?: emptyList()
        }
    }
}