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

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies

    fun fetchMovies(page: Int = 1) {
        viewModelScope.launch {
            _movies.value = try {
                repository.getTopMovies(page)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

class HomepageViewModelFactory(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomepageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomepageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}