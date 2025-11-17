package com.example.skillcinema.ui.moviedetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.CrewMember
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CrewListViewModel(private val repository: MovieDetailRepository) : ViewModel() {

    private val _crew = MutableStateFlow<List<CrewMember>>(emptyList())
    val crew: StateFlow<List<CrewMember>> = _crew.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadCrew(movieId: Int) {
        if (movieId == 0) {
            _errorMessage.value = "ID фильма не найден"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val staff = repository.getMovieCast(movieId)
                val crewMembers = staff
                    .filter { it.professionKey != "ACTOR" }
                    .map { person ->
                        CrewMember(
                            id = person.staffId,
                            name = person.nameRu ?: person.nameEn ?: "Неизвестный участник",
                            role = person.profession ?: "",
                            photoUrl = person.posterUrl
                        )
                    }
                _crew.value = crewMembers
            } catch (e: Exception) {
                _crew.value = emptyList()
                _errorMessage.value = e.message ?: "Не удалось загрузить список съёмочной группы"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CrewListViewModelFactory(
    private val repository: MovieDetailRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CrewListViewModel::class.java)) {
            return CrewListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}