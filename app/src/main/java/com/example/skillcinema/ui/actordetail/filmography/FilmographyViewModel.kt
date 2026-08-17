// ui/actordetail/filmography/FilmographyViewModel.kt
package com.example.skillcinema.ui.actordetail.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.domain.repositories.ActorRepository
import com.example.skillcinema.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Одна ViewModel на весь экран фильмографии: список табов строится по ключам карты,
 * а вкладки ([FilmographyTabFragment]) читают из неё свою часть фильмографии.
 */
class FilmographyViewModel(
    private val repository: ActorRepository
) : ViewModel() {

    private val _filmography =
        MutableStateFlow<Resource<Map<Profession, List<Film>>>>(Resource.Loading())
    val filmography = _filmography.asStateFlow()

    private var loadedActorId: Int? = null

    fun loadFilmography(actorId: Int) {
        if (loadedActorId == actorId && _filmography.value is Resource.Success) return
        loadedActorId = actorId

        viewModelScope.launch {
            _filmography.value = Resource.Loading()
            try {
                _filmography.value = Resource.Success(repository.getFilmography(actorId))
            } catch (e: Exception) {
                loadedActorId = null
                _filmography.value = Resource.Error(e.message ?: "Error loading filmography")
            }
        }
    }
}
