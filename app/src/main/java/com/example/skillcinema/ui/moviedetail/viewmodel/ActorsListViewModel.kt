package com.example.skillcinema.ui.moviedetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Actor
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActorsListViewModel(private val repository: MovieDetailRepository) : ViewModel() {

    private val _actors = MutableStateFlow<List<Actor>>(emptyList())
    val actors: StateFlow<List<Actor>> = _actors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadActors(movieId: Int) {
        if (movieId == 0) {
            _errorMessage.value = "ID фильма не найден"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val cast = repository.getMovieCast(movieId)
                val actors = cast
                    .filter { it.professionKey == "ACTOR" }
                    .map { actor ->
                        Actor(
                            id = actor.staffId,
                            name = actor.nameRu ?: actor.nameEn ?: "Неизвестный актер",
                            role = actor.description ?: "Роль не указана",
                            photoUrl = actor.posterUrl,
                            profession = actor.professionKey
                        )
                    }
                _actors.value = actors
            } catch (e: Exception) {
                _actors.value = emptyList()
                _errorMessage.value = e.message ?: "Не удалось загрузить актеров"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ActorsListViewModelFactory(
    private val repository: MovieDetailRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorsListViewModel::class.java)) {
            return ActorsListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}