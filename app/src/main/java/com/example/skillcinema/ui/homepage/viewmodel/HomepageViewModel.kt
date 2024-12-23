package com.example.skillcinema.ui.homepage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomepageViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList()) // Инициализация с пустым списком
    val movies: StateFlow<List<Movie>> get() = _movies

    fun fetchMovies() {
        viewModelScope.launch {
            val fetchedMovies = repository.getTopMovies(page = 1)
            _movies.value = fetchedMovies
        }
    }
}