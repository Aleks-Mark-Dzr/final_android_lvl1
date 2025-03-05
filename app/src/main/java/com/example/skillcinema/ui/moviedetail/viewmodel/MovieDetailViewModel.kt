package com.example.skillcinema.ui.moviedetail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.data.ActorResponse
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

class MovieDetailViewModel(private val repository: MovieDetailRepository) : ViewModel() {

    private val _movieDetail = MutableStateFlow<MovieDetailResponse?>(null)
    val movieDetail: StateFlow<MovieDetailResponse?> get() = _movieDetail.asStateFlow()

    private val _actorsList = MutableStateFlow<List<ActorResponse>>(emptyList())
    val actorsList: StateFlow<List<ActorResponse>> get() = _actorsList.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> get() = _isFavorite.asStateFlow()

    private val _isWatchLater = MutableStateFlow(false)
    val isWatchLater: StateFlow<Boolean> get() = _isWatchLater.asStateFlow()

    private val _isWatched = MutableStateFlow(false)
    val isWatched: StateFlow<Boolean> get() = _isWatched.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            Log.d("MovieDetailViewModel", "Запрос деталей фильма для ID: $movieId")

            val movieDeferred = async {
                Log.d("MovieDetailViewModel", "Отправляем запрос в API: /api/v2.2/films/$movieId")
                repository.getMovieDetails(movieId)
            }

            val actorsDeferred = async {
                Log.d("MovieDetailViewModel", "Отправляем запрос в API: /api/v1/staff?filmId=$movieId")
                repository.getMovieCast(movieId)
            }

            _movieDetail.value = movieDeferred.await()
            _actorsList.value = actorsDeferred.await()

            Log.d("MovieDetailViewModel", "Фильм загружен: ${_movieDetail.value}")
            Log.d("MovieDetailViewModel", "Актеры загружены: ${_actorsList.value?.size} актеров")
        }
    }


    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            _isFavorite.value = !_isFavorite.value
        }
    }

    fun toggleWatchLater(movieId: Int) {
        viewModelScope.launch {
            _isWatchLater.value = !_isWatchLater.value
        }
    }

    fun toggleWatched(movieId: Int) {
        viewModelScope.launch {
            _isWatched.value = !_isWatched.value
        }
    }
}