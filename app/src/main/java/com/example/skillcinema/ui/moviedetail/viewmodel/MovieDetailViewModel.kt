package com.example.skillcinema.ui.moviedetail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.ActorResponse
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.data.MovieImage
import com.example.skillcinema.data.MovieImagesResponse
import com.example.skillcinema.data.repository.MovieDetailRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class MovieDetailViewModel(private val repository: MovieDetailRepository) : ViewModel() {

    private val _movieDetail = MutableStateFlow<MovieDetailResponse?>(null)
    val movieDetail: StateFlow<MovieDetailResponse?> = _movieDetail.asStateFlow()

    private val _actorsList = MutableStateFlow<List<ActorResponse>>(emptyList())
    val actorsList: StateFlow<List<ActorResponse>> = _actorsList.asStateFlow()

    private val _crewList = MutableStateFlow<List<ActorResponse>>(emptyList())
    val crewList: StateFlow<List<ActorResponse>> = _crewList.asStateFlow()

    private val _shootingImages = MutableStateFlow<List<MovieImage>>(emptyList())
    val shootingImages: StateFlow<List<MovieImage>> = _shootingImages.asStateFlow()

    private val _posterImages = MutableStateFlow<List<MovieImage>>(emptyList())
    val posterImages: StateFlow<List<MovieImage>> = _posterImages.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isWatchLater = MutableStateFlow(false)
    val isWatchLater: StateFlow<Boolean> = _isWatchLater.asStateFlow()

    private val _isWatched = MutableStateFlow(false)
    val isWatched: StateFlow<Boolean> = _isWatched.asStateFlow()

    // ❗ ОСТАВИТЬ ТОЛЬКО ЭТУ ПАРУ
    private val _errorMessages = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            Log.d("MovieDetailViewModel", "Запрос деталей фильма для ID: $movieId")

            try {
                val result = supervisorScope {
                    val movieDeferred = async {
                        Log.d("MovieDetailViewModel", "API: /api/v2.2/films/$movieId")
                        repository.getMovieDetails(movieId)
                    }
                    val staffDeferred = async {
                        Log.d("MovieDetailViewModel", "API: /api/v1/staff?filmId=$movieId")
                        repository.getMovieCast(movieId)
                    }
                    val shootingDeferred = async {
                        Log.d("MovieDetailViewModel", "API: /api/v2.2/films/$movieId/images?type=SHOOTING&page=1")
                        repository.getMovieImages(movieId, "SHOOTING")
                    }
                    val posterDeferred = async {
                        Log.d("MovieDetailViewModel", "API: /api/v2.2/films/$movieId/images?type=POSTER&page=1")
                        repository.getMovieImages(movieId, "POSTER")
                    }

                    val movie = movieDeferred.await()

                    val staff = try {
                        staffDeferred.await()
                    } catch (error: Exception) {
                        Log.e("MovieDetailViewModel", "Ошибка при загрузке актеров", error)
                        emptyList()
                    }

                    val shooting = try {
                        shootingDeferred.await()
                    } catch (error: Exception) {
                        Log.e("MovieDetailViewModel", "Ошибка при загрузке съемочных изображений", error)
                        null
                    }

                    val posters = try {
                        posterDeferred.await()
                    } catch (error: Exception) {
                        Log.e("MovieDetailViewModel", "Ошибка при загрузке постеров", error)
                        null
                    }

                    MovieDetailResult(movie, staff, shooting, posters)
                }

                val movie = result.movie
                if (movie == null) {
                    Log.e("MovieDetailViewModel", "Не удалось получить данные фильма из API и кэша")
                    _movieDetail.value = null
                    _actorsList.value = emptyList()
                    _crewList.value = emptyList()
                    _errorMessages.tryEmit("Не удалось загрузить данные фильма")
                    return@launch
                }

                _movieDetail.value = movie

                val (actors, crew) = result.staff.partition { it.professionKey == "ACTOR" }
                _actorsList.value = actors
                _crewList.value = crew
                _shootingImages.value = result.shooting?.items.orEmpty()
                _posterImages.value = result.posters?.items.orEmpty()

                Log.d("MovieDetailViewModel", "Фильм загружен: ${movie.nameRu}")
                Log.d(
                    "MovieDetailViewModel",
                    "Загружено актеров: ${actors.size}, съемочной группы: ${crew.size}, съемочных фото: ${_shootingImages.value.size}, постеров: ${_posterImages.value.size}"
                )

                repository.getMovieById(movieId)?.let {
                    _isFavorite.value = it.isFavorite
                    _isWatchLater.value = it.isWatchLater
                    _isWatched.value = it.isWatched
                }
            } catch (e: Exception) {
                Log.e("MovieDetailViewModel", "Ошибка при загрузке данных фильма", e)
                _movieDetail.value = null
                _actorsList.value = emptyList()
                _crewList.value = emptyList()
                _errorMessages.tryEmit("Произошла ошибка при загрузке данных фильма")
            }
        }
    }

    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            val currentStatus = !_isFavorite.value
            _isFavorite.value = currentStatus
            repository.updateFavoriteStatus(movieId, currentStatus)
        }
    }

    fun toggleWatchLater(movieId: Int) {
        viewModelScope.launch {
            val currentStatus = !_isWatchLater.value
            _isWatchLater.value = currentStatus
            repository.updateWatchLaterStatus(movieId, currentStatus)
        }
    }

    fun toggleWatched(movieId: Int) {
        viewModelScope.launch {
            val currentStatus = !_isWatched.value
            _isWatched.value = currentStatus
            repository.updateWatchedStatus(movieId, currentStatus)
        }
    }

    // 👇 Вынесено на уровень класса (можно сделать и top-level вне класса)
    private data class MovieDetailResult(
        val movie: MovieDetailResponse?,
        val staff: List<ActorResponse>,
        val shooting: MovieImagesResponse?,
        val posters: MovieImagesResponse?
    )
}