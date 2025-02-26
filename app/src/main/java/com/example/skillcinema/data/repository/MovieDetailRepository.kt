package com.example.skillcinema.data.repository

import android.util.Log
import com.example.skillcinema.data.*
import com.example.skillcinema.data.database.MovieDao
import com.example.skillcinema.data.database.MovieEntity
import com.example.skillcinema.network.MovieApiService
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface MovieDetailRepository {
    suspend fun getMovieDetails(movieId: Int): MovieDetailResponse?
    suspend fun getMovieCast(movieId: Int): List<ActorResponse>
    suspend fun getMovieGallery(movieId: Int): GalleryResponse
    suspend fun getSeasons(movieId: Int): SeasonsResponse
    suspend fun getSimilarMovies(movieId: Int): SimilarMoviesResponse
}

class MovieDetailRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieDetailRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun getMovieDetails(movieId: Int): MovieDetailResponse? {
        return withContext(repositoryScope.coroutineContext) {
            try {
                Log.d("MovieDetailRepository", "📤 Запрос фильма с ID: $movieId")

                // Проверяем локальную базу
                val cachedMovie = movieDao.getMovieById(movieId)
                if (cachedMovie != null) {
                    Log.d("MovieDetailRepository", "✅ Фильм найден в локальной базе: ${cachedMovie.nameRu}")
                    return@withContext cachedMovie.toMovieDetailResponse()
                }

                Log.d("MovieDetailRepository", "🌐 Запрашиваем фильм из API...")

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

                if (movie == null) {
                    Log.e("MovieDetailRepository", "❌ Фильм не удалось загрузить после 3 попыток")
                    return@withContext null
                }

                Log.d("MovieDetailRepository", "✅ Успешно загружен фильм: ${movie!!.nameRu}")

                // Сохраняем в локальную базу
                movieDao.insertMovie(movie!!.toMovieEntity())
                Log.d("MovieDetailRepository", "💾 Фильм сохранен в локальную базу")

                return@withContext movie
            } catch (e: HttpException) {
                Log.e("MovieDetailRepository", "❌ HTTP Ошибка: ${e.code()} - ${e.message()}", e)
                null
            } catch (e: IOException) {
                Log.e("MovieDetailRepository", "❌ Ошибка сети: ${e.message}", e)
                null
            } catch (e: CancellationException) {
                Log.e("MovieDetailRepository", "⏳ Запрос отменен", e)
                null
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "❌ Ошибка при получении фильма: ${e.message}", e)
                null
            }
        }
    }

    override suspend fun getMovieCast(movieId: Int): List<ActorResponse> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieDetailRepository", "🎭 Запрос актеров для фильма ID: $movieId")
                apiService.getMovieCast(movieId)
            } catch (e: Exception) {
                Log.e("MovieDetailRepository", "❌ Ошибка загрузки актеров: ${e.message}")
                emptyList()
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
}

// ✅ Конвертация `MovieEntity` → `MovieDetailResponse`
private fun MovieEntity.toMovieDetailResponse(): MovieDetailResponse {
    return MovieDetailResponse(
        kinopoiskId = this.movieId,
        nameRu = this.nameRu,
        nameOriginal = this.nameOriginal,
        year = this.year,
        posterUrl = this.posterUrl,
        description = null,
        ratingKinopoisk = this.rating,
        genres = emptyList(),
        countries = emptyList(),
        filmLength = null,
        serial = false,
        seasonsCount = null,
        ratingAgeLimits = null
    )
}

// ✅ Конвертация `MovieDetailResponse` → `MovieEntity`
private fun MovieDetailResponse.toMovieEntity(): MovieEntity {
    return MovieEntity(
        movieId = this.kinopoiskId,
        nameRu = this.nameRu ?: "Неизвестно",
        nameOriginal = this.nameOriginal ?: "",
        year = this.year ?: "",
        posterUrl = this.posterUrl ?: "",
        rating = this.ratingKinopoisk ?: 0.0,
        isWatched = false,
        isFavorite = false
    )
}