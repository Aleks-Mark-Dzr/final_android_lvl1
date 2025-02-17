package com.example.skillcinema.data.repository

import com.example.skillcinema.data.ActorResponse
import com.example.skillcinema.data.GalleryResponse
import com.example.skillcinema.data.GalleryItem
import com.example.skillcinema.data.MovieDetailResponse
import com.example.skillcinema.data.SeasonsResponse
import com.example.skillcinema.data.SimilarMoviesResponse
import com.example.skillcinema.data.database.MovieDao
import com.example.skillcinema.data.database.MovieEntity
import com.example.skillcinema.network.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun getMovieDetails(movieId: Int): MovieDetailResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Сначала проверяем локальную базу
                val cachedMovie = movieDao.getMovieById(movieId)
                if (cachedMovie != null) {
                    return@withContext MovieDetailResponse(
                        kinopoiskId = cachedMovie.movieId,
                        nameRu = cachedMovie.nameRu,
                        nameOriginal = cachedMovie.nameOriginal,
                        year = cachedMovie.year,
                        posterUrl = cachedMovie.posterUrl,
                        description = null,
                        ratingKinopoisk = cachedMovie.rating,
                        genres = emptyList(),
                        countries = emptyList(),
                        filmLength = null,
                        serial = false,
                        seasonsCount = null
                    )
                }

                // 2. Если нет в базе – загружаем из API
                val movie = apiService.getMovieDetails(movieId)

                // 3. Сохраняем фильм в локальную базу данных
                movieDao.insertMovie(
                    MovieEntity(
                        movieId = movie.kinopoiskId,
                        nameRu = movie.nameRu ?: "Неизвестно",
                        nameOriginal = movie.nameOriginal ?: "",
                        year = movie.year ?: "",
                        posterUrl = movie.posterUrl ?: "",
                        rating = movie.ratingKinopoisk ?: 0.0,
                        isWatched = false,
                        isFavorite = false
                    )
                )

                return@withContext movie

            } catch (e: Exception) {
                e.printStackTrace()
                null // Возвращаем `null`, если запрос не удался
            }
        }
    }

    override suspend fun getMovieCast(movieId: Int): List<ActorResponse> =
        withContext(Dispatchers.IO) {
            try {
                apiService.getMovieCast(movieId)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Возвращаем пустой список, если запрос не удался
            }
        }

    override suspend fun getMovieGallery(movieId: Int): GalleryResponse =
        withContext(Dispatchers.IO) {
            try {
                apiService.getMovieGallery(movieId)
            } catch (e: Exception) {
                e.printStackTrace()
                GalleryResponse(total =20, emptyList()) // Возвращаем пустую галерею
            }
        }

    override suspend fun getSeasons(movieId: Int): SeasonsResponse =
        withContext(Dispatchers.IO) {
            try {
                apiService.getSeasons(movieId)
            } catch (e: Exception) {
                e.printStackTrace()
                SeasonsResponse(0, emptyList()) // Возвращаем пустые сезоны
            }
        }

    override suspend fun getSimilarMovies(movieId: Int): SimilarMoviesResponse =
        withContext(Dispatchers.IO) {
            try {
                apiService.getSimilarMovies(movieId)
            } catch (e: Exception) {
                e.printStackTrace()
                SimilarMoviesResponse(emptyList()) // Возвращаем пустой список похожих фильмов
            }
        }
}