package com.example.skillcinema.ui.profile.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionMoviesViewModel(
    private val movieDetailRepository: MovieDetailRepository,
    private val collectionId: Int
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    init {
        observeCollectionMovies()
    }

    private fun observeCollectionMovies() {
        viewModelScope.launch {
            movieDetailRepository.getMoviesByCollectionId(collectionId).collect { movies ->
                _movies.value = movies
            }
        }
    }
}