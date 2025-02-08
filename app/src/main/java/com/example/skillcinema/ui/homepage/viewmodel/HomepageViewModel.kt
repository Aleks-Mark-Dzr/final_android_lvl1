package com.example.skillcinema.ui.homepage.viewmodel

import androidx.lifecycle.ViewModel
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

    private val _top250Movies = MutableStateFlow<List<Movie>>(emptyList())
    val top250Movies: StateFlow<List<Movie>> get() = _top250Movies

    private val _tvSeries = MutableStateFlow<List<Movie>>(emptyList())
    val tvSeries: StateFlow<List<Movie>> get() = _tvSeries

    fun fetchPremieres(year: Int, month: String) {
        viewModelScope.launch {
            _premieres.value = repository.getPremieres(year, month)?.take(20) ?: emptyList()
        }
    }

    fun fetchPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            _popularMovies.value = repository.getTopMovies(page)?.take(20) ?: emptyList()
        }
    }

    fun fetchDynamicCategory(countryId: Int, genreId: Int) {
        viewModelScope.launch {
            _dynamicCategory.value =
                repository.getMoviesByGenreAndCountry(countryId, genreId)?.take(20) ?: emptyList()
        }
    }

    fun fetchTop250Movies(page: Int = 1) {
        viewModelScope.launch {
            _top250Movies.value = repository.getTop250Movies(page)?.take(20) ?: emptyList()
        }
    }

    fun fetchTvSeries(page: Int = 1) {
        viewModelScope.launch {
            _tvSeries.value = repository.getTvSeries(page)?.take(20) ?: emptyList()
        }
    }
}