package com.example.skillcinema.ui.profile.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.data.repository.MovieDetailRepository

class CollectionMoviesViewModelFactory(
    private val movieDetailRepository: MovieDetailRepository,
    private val collectionId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionMoviesViewModel::class.java)) {
            return CollectionMoviesViewModel(movieDetailRepository, collectionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}