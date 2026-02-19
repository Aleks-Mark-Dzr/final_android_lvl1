package com.example.skillcinema.ui.profile.wanttosee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WantToSeeViewModel(
    private val movieDetailRepository: MovieDetailRepository
) : ViewModel() {

    private val _watchLaterMovies = MutableStateFlow<List<Movie>>(emptyList())
    val watchLaterMovies: StateFlow<List<Movie>> = _watchLaterMovies.asStateFlow()

    init {
        observeWatchLaterMovies()
    }

    private fun observeWatchLaterMovies() {
        viewModelScope.launch {
            movieDetailRepository.getWatchLaterMovies().collect { movies ->
                _watchLaterMovies.value = movies
            }
        }
    }
}