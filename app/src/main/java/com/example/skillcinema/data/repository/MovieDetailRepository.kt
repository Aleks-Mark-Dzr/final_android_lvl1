package com.example.skillcinema.data.repository

import android.util.Log
import com.example.skillcinema.data.*
import com.example.skillcinema.data.database.MovieDao
import com.example.skillcinema.data.database.MovieEntity
import com.example.skillcinema.network.MovieApiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface MovieDetailRepository {
    suspend fun getMovieDetails(movieId: Int): MovieDetailResponse?
    suspend fun getMovieCast(movieId: Int): List<ActorResponse>
    suspend fun getMovieGallery(movieId: Int): GalleryResponse
    suspend fun getSeasons(movieId: Int): SeasonsResponse
    suspend fun getSimilarMovies(movieId: Int): SimilarMoviesResponse
    suspend fun updateFavoriteStatus(movieId: Int, favorite: Boolean)
    suspend fun updateWatchedStatus(movieId: Int, watched: Boolean)
    suspend fun updateWatchLaterStatus(movieId: Int, watchLater: Boolean)
    suspend fun getMovieById(movieId: Int): MovieEntity?
    fun getFavoriteMovies(): Flow<List<Movie>>
}

class MovieDetailRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieDetailRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun getMovieDetails(movieId: Int): MovieDetailResponse? {
        return withContext(repositoryScope.coroutineContext) {
            var cachedMovie: MovieEntity? = null
            try {
                Log.d("MovieDetailRepository", "📤 Запрос фильма с ID: $movieId")

                cachedMovie = movieDao.getMovieById(movieId)
                if (cachedMovie != null) {
                    Log.d(
                        "MovieDetailRepository",
                        "✅ Фильм найден в локальной базе: ${cachedMovie.nameRu}. Все равно запрашиваем свежие данные из API"
                    )
                } else {
                    Log.d("MovieDetailRepository", "🌐 Фильма нет в локальной базе, запрашиваем из API...")
                }

                var movie: MovieDetailResponse? = null

                // Повторяем запрос до 3 раз при `TimeoutCancellationException`
                repeat(3) { attempt ->
                    try {
                        movie = withTimeoutOrNull(7000) { apiService.getMovieDetails(movieId) }
                        if (movie != null) {
                            Log.d("MovieDetailRepository", "✅ Фильм загружен с попытки ${attempt + 1}")
                            return@repeat
                        } else {
                            Log.w("MovieDetailRepository", "⚠️ Попытка ${attempt + 1} не удалась, повторяем...")
                            delay(2000)
                        }
                    } catch (e: TimeoutCancellationException) {
                        Log.w("MovieDetailRepository", "⏳ Тайм-аут на попытке ${attempt + 1}, повторяем...")
                    }
                }

                if (movie != null) {
                    Log.d("MovieDetailRepository", "✅ Успешно загружен фильм: ${movie!!.nameRu}")

                    // Сохраняем в локальную базу, сохраняя пользовательские флаги
                    movieDao.insertMovie(movie!!.toMovieEntity(cachedMovie))
                    Log.d("MovieDetailRepository", "💾 Фильм сохранен в локальную базу")

                    return@withContext movie
                }

                Log.e("MovieDetailRepository", "❌ Фильм не удалось загрузить после 3 попыток")

                // Если API недоступно, пытаемся вернуть данные из локальной базы
                if (cachedMovie != null) {
                    Log.w(
                        "MovieDetailRepository",
                        "⚠️ Возвращаем кэшированные данные (без дополнительной информации из API)"
                    )
                    return@withContext cachedMovie.toMovieDetailResponse()
                }

                return@withContext null
            } catch (e: HttpException) {
                Log.e("MovieDetailRepository", "❌ HTTP Ошибка: ${e.code()} - ${e.message()}", e)
                cachedMovie?.toMovieDetailResponse()
            } catch (e: IOException) {
                Log.e("MovieDetailRepository", "❌ Ошибка сети: ${e.message}", e)
                cachedMovie?.toMovieDetailResponse()
            } catch (e: CancellationException) {
                Log.e("MovieDetailRepository", "⏳ Запрос отменен", e)
                cachedMovie?.toMovieDetailResponse()
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "❌ Ошибка при получении фильма: ${e.message}", e)
                cachedMovie?.toMovieDetailResponse()
            }
        }
    }

    override suspend fun getMovieCast(movieId: Int): List<ActorResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieDetailRepository", "Запрос к API: /api/v1/staff?filmId=$movieId")
                val response = apiService.getMovieCast(movieId)
                Log.d("MovieDetailRepository", "Ответ API: ${response.size} актеров")
                response
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "Ошибка загрузки актеров: ${e.message}")
                emptyList()
            }
        }
    }

    override suspend fun getMovieGallery(movieId: Int): GalleryResponse =
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieDetailRepository", "🖼️ Запрос галереи для фильма ID: $movieId")
                apiService.getMovieGallery(movieId)
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "❌ Ошибка загрузки галереи: ${e.message}")
                GalleryResponse(total = 0, items = emptyList())
            }
        }

    override suspend fun getSeasons(movieId: Int): SeasonsResponse =
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieDetailRepository", "📺 Запрос сезонов для фильма ID: $movieId")
                apiService.getSeasons(movieId)
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "❌ Ошибка загрузки сезонов: ${e.message}")
                SeasonsResponse(0, emptyList())
            }
        }

    override suspend fun getSimilarMovies(movieId: Int): SimilarMoviesResponse =
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieDetailRepository", "🎬 Запрос похожих фильмов для ID: $movieId")
                apiService.getSimilarMovies(movieId)
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "❌ Ошибка загрузки похожих фильмов: ${e.message}")
                SimilarMoviesResponse(emptyList())
            }
        }

    override suspend fun updateFavoriteStatus(movieId: Int, favorite: Boolean) {
        movieDao.updateFavoriteStatus(movieId, favorite)
    }

    override suspend fun updateWatchedStatus(movieId: Int, watched: Boolean) {
        movieDao.updateWatchedStatus(movieId, watched)
    }

    override suspend fun updateWatchLaterStatus(movieId: Int, watchLater: Boolean) {
        movieDao.updateWatchLaterStatus(movieId, watchLater)
    }

    override suspend fun getMovieById(movieId: Int): MovieEntity? {
        return movieDao.getMovieById(movieId)
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDao.getFavoriteMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }
}

// ✅ Конвертация `MovieEntity` → `MovieDetailResponse`
private fun MovieEntity.toMovieDetailResponse(): MovieDetailResponse {
    return MovieDetailResponse(
        kinopoiskId = this.movieId,
        nameRu = this.nameRu,
        nameOriginal = this.nameOriginal,
        year = this.year,
        posterUrl = this.posterUrl,
        description = this.description,
        ratingKinopoisk = this.rating,
        genres = emptyList(),
        countries = emptyList(),
        filmLength = null,
        serial = false,
        seasonsCount = null,
        ratingAgeLimits = null,
    )
}

private fun MovieEntity.toMovie(): Movie {
    return Movie(
        filmId = this.movieId,
        kinopoiskId = this.movieId,
        nameRu = this.nameRu,
        year = this.year ?: "",
        posterUrlPreview = this.posterUrl ?: "",
        rating = this.rating,
        ratingKinopoisk = this.rating,
        genres = emptyList()
    )
}

// ✅ Конвертация `MovieDetailResponse` → `MovieEntity`
private fun MovieDetailResponse.toMovieEntity(previous: MovieEntity?): MovieEntity {
    return MovieEntity(
        movieId = this.kinopoiskId,
        nameRu = this.nameRu ?: previous?.nameRu ?: "Неизвестно",
        nameOriginal = this.nameOriginal ?: previous?.nameOriginal,
        year = this.year ?: previous?.year,
        posterUrl = this.posterUrl ?: previous?.posterUrl,
        rating = this.ratingKinopoisk ?: previous?.rating,
        isWatched = previous?.isWatched ?: false,
        isFavorite = previous?.isFavorite ?: false,
        isWatchLater = previous?.isWatchLater ?: false,
        description = this.description ?: previous?.description
    )
}