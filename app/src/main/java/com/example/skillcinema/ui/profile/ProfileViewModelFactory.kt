package com.example.skillcinema.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.data.repository.MovieDetailRepository
import com.example.skillcinema.data.repository.ProfileRepository


class ProfileViewModelFactory(
    private val movieDetailRepository: MovieDetailRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(movieDetailRepository, profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}