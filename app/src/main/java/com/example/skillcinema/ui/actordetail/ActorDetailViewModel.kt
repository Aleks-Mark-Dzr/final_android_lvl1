package com.example.skillcinema.ui.actordetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Actor
//import com.example.skillcinema.domain.models.Actor
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.repositories.ActorRepository
import com.example.skillcinema.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActorDetailViewModel @Inject constructor(
    private val repository: ActorRepository
) : ViewModel() {

    private val _actorDetails = MutableStateFlow<Resource<Actor>>(Resource.Loading())
    val actorDetails = _actorDetails

    private val _topFilms = MutableStateFlow<List<Film>>(emptyList())
    val topFilms = _topFilms

    fun loadActorData(actorId: Int) {
        viewModelScope.launch {
            _actorDetails.value = Resource.Loading()
            try {
                val actor = repository.getActorDetails(actorId)
                _actorDetails.value = Resource.Success(actor)
                _topFilms.value = repository.getTopFilms(actorId)
            } catch (e: Exception) {
                _actorDetails.value = Resource.Error(e.message ?: "Error loading actor")
            }
        }
    }
}