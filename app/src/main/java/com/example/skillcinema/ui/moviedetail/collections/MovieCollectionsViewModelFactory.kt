package com.example.skillcinema.ui.moviedetail.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.data.repository.MovieDetailRepository

class MovieCollectionsViewModelFactory(
    private val repository: MovieDetailRepository,
    private val movieId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieCollectionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieCollectionsViewModel(repository, movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}