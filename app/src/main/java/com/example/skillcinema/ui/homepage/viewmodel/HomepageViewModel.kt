package com.example.skillcinema.ui.homepage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomepageViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> get() = _movies

    fun fetchMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                _movies.value = repository.getTopMovies(page)
            } catch (e: Exception) {
                Log.e("HomepageViewModel", "Error fetching movies: ${e.message}", e)
                _movies.value = emptyList()
            }
        }
    }
}