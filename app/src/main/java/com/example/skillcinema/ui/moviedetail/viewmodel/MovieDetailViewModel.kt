package com.example.skillcinema.ui.moviedetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val repository: MovieDetailRepository) : ViewModel() {

    private val _movieDetail = MutableStateFlow<MovieDetailResponse?>(null)
    val movieDetail: StateFlow<MovieDetailResponse?> get() = _movieDetail.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> get() = _isFavorite.asStateFlow()

    private val _isWatched = MutableStateFlow(false)
    val isWatched: StateFlow<Boolean> get() = _isWatched.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            val movie = repository.getMovieDetails(movieId)
            _movieDetail.value = movie
        }
    }

    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            _isFavorite.value = !_isFavorite.value
        }
    }

    fun toggleWatched(movieId: Int) {
        viewModelScope.launch {
            _isWatched.value = !_isWatched.value
        }
    }
}