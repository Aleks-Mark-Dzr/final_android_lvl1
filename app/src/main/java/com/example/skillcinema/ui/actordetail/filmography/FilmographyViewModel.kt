// ui/actordetail/filmography/FilmographyViewModel.kt
package com.example.skillcinema.ui.actordetail.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.domain.repositories.ActorRepository
import com.example.skillcinema.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    private val repository: ActorRepository
) : ViewModel() {

    private val _films = MutableStateFlow<Resource<List<Film>>>(Resource.Loading())
    val films = _films

    fun loadFilmography(actorId: Int, profession: Profession) {
        viewModelScope.launch {
            _films.value = Resource.Loading()
            try {
                val films = repository.getFilmsByProfession(actorId, profession)
                _films.value = Resource.Success(films)
            } catch (e: Exception) {
                _films.value = Resource.Error(e.message ?: "Error loading filmography")
            }
        }
    }
}