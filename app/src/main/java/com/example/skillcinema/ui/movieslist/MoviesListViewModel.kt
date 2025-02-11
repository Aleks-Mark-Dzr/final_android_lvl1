package com.example.skillcinema.ui.movieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val getPremieresUseCase: GetPremieresUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getMoviesByGenreAndCountryUseCase: GetMoviesByGenreAndCountryUseCase,
    private val getTop250MoviesUseCase: GetTop250MoviesUseCase,
    private val getTvSeriesUseCase: GetTvSeriesUseCase
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies.asStateFlow()

    fun loadMoviesForCategory(category: String) {
        viewModelScope.launch {
            try {
                val moviesList = when (category) {
                    "Премьеры" -> getPremieresUseCase.execute(2025, "January")
                    "Популярное" -> getPopularMoviesUseCase.execute(1)
                    "Динамическая подборка" -> getMoviesByGenreAndCountryUseCase.execute(1, 2)
                    "Топ-250" -> getTop250MoviesUseCase.execute(1)
                    "Сериалы" -> getTvSeriesUseCase.execute(1)
                    else -> emptyList()
                }

                // Обновляем StateFlow только если данные изменились
                if (_movies.value != moviesList) {
                    _movies.emit(moviesList)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _movies.emit(emptyList()) // Безопасная обработка ошибок
            }
        }
    }

}