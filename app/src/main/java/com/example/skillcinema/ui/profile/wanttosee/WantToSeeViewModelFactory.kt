package com.example.skillcinema.ui.profile.wanttosee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.data.repository.MovieDetailRepository

class WantToSeeViewModelFactory(
    private val movieDetailRepository: MovieDetailRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WantToSeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WantToSeeViewModel(movieDetailRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}