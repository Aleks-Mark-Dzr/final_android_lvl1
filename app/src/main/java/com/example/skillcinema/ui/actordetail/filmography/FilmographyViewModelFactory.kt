package com.example.skillcinema.ui.actordetail.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.domain.repositories.ActorRepository

class FilmographyViewModelFactory(
    private val repository: ActorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmographyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilmographyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}