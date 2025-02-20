package com.example.skillcinema.ui.homepage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomepageViewModel(
    private val getPremieresUseCase: GetPremieresUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getMoviesByGenreAndCountryUseCase: GetMoviesByGenreAndCountryUseCase,
    private val getTop250MoviesUseCase: GetTop250MoviesUseCase,
    private val getTvSeriesUseCase: GetTvSeriesUseCase
) : ViewModel() {

    private val _premieres = MutableStateFlow<List<Movie>>(emptyList())
    val premieres: StateFlow<List<Movie>> get() = _premieres.asStateFlow()

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> get() = _popularMovies.asStateFlow()

    private val _dynamicCategory = MutableStateFlow<List<Movie>>(emptyList())
    val dynamicCategory: StateFlow<List<Movie>> get() = _dynamicCategory.asStateFlow()

    private val _top250Movies = MutableStateFlow<List<Movie>>(emptyList())
    val top250Movies: StateFlow<List<Movie>> get() = _top250Movies.asStateFlow()

    private val _tvSeries = MutableStateFlow<List<Movie>>(emptyList())
    val tvSeries: StateFlow<List<Movie>> get() = _tvSeries.asStateFlow()

    fun fetchPremieres(year: Int, month: String) {
        viewModelScope.launch {
            _premieres.value = getPremieresUseCase.execute(year, month).shuffled()
        }
    }

    fun fetchPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            _popularMovies.value = getPopularMoviesUseCase.execute(page).shuffled()
        }
    }

    fun fetchDynamicCategory(countryId: Int, genreId: Int) {
        viewModelScope.launch {
            _dynamicCategory.value = getMoviesByGenreAndCountryUseCase.execute(countryId, genreId).shuffled()
        }
    }

    fun fetchTop250Movies(page: Int = 1) {
        viewModelScope.launch {
            _top250Movies.value = getTop250MoviesUseCase.execute(page).shuffled()
        }
    }

    fun fetchTvSeries(page: Int = 1) {
        viewModelScope.launch {
            _tvSeries.value = getTvSeriesUseCase.execute(page).shuffled()
        }
    }
}