package com.example.skillcinema.ui.actordetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Actor
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.repositories.ActorRepository
import com.example.skillcinema.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActorDetailViewModel(
    private val repository: ActorRepository
) : ViewModel() {

    private val _actorDetails = MutableStateFlow<Resource<Actor>>(Resource.Loading())
    val actorDetails = _actorDetails.asStateFlow()

    private val _topFilms = MutableStateFlow<List<Film>>(emptyList())
    val topFilms = _topFilms.asStateFlow()

    /** Общее количество фильмов в фильмографии — показывается под кнопкой «Фильмография». */
    private val _filmsCount = MutableStateFlow(0)
    val filmsCount = _filmsCount.asStateFlow()

    private var loadedActorId: Int? = null

    fun loadActorData(actorId: Int) {
        if (loadedActorId == actorId && _actorDetails.value is Resource.Success) return
        loadedActorId = actorId

        viewModelScope.launch {
            _actorDetails.value = Resource.Loading()
            try {
                _actorDetails.value = Resource.Success(repository.getActorDetails(actorId))

                val filmography = repository.getFilmography(actorId)
                _filmsCount.value = filmography.values
                    .flatten()
                    .distinctBy { it.id }
                    .size

                _topFilms.value = repository.getTopFilms(actorId)
            } catch (e: Exception) {
                loadedActorId = null
                _actorDetails.value = Resource.Error(e.message ?: "Error loading actor")
            }
        }
    }
}
