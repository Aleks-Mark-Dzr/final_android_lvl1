package com.example.skillcinema.ui.actordetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillcinema.domain.repositories.ActorRepository

class ActorDetailViewModelFactory(
    private val repository: ActorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActorDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}