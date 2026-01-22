package com.example.skillcinema.data.repository

import android.util.Log
import com.example.skillcinema.data.Actor
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.domain.repositories.ActorRepository
import com.example.skillcinema.network.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class ActorRepositoryImpl(
    private val apiService: MovieApiService
) : ActorRepository {

    override suspend fun getActorDetails(actorId: Int): Actor = withContext(Dispatchers.IO) {
        val response = apiService.getActorDetails(actorId)
        Actor(
            id = response.id,
            name = response.nameRu ?: response.nameEn ?: "Неизвестный актер",
            role = "",
            photoUrl = response.photoUrl,
            profession = response.profession
        )
    }

    override suspend fun getTopFilms(actorId: Int): List<Film> = withContext(Dispatchers.IO) {
        val response = apiService.getActorDetails(actorId)
        val ratedFilms = response.filmography
            .map { film ->
                FilmSeed(
                    id = film.id,
                    title = film.titleRu ?: film.titleEn ?: "Без названия",
                    rating = film.rating?.toDoubleOrNull()
                )
            }
            .sortedByDescending { it.rating ?: Double.NEGATIVE_INFINITY }
            .take(10)

        coroutineScope {
            ratedFilms.map { seed ->
                async {
                    try {
                        val detail = apiService.getMovieDetails(seed.id)
                        Film(
                            id = seed.id,
                            title = detail.nameRu ?: detail.nameOriginal ?: seed.title,
                            year = detail.year ?: "",
                            posterUrl = detail.posterUrl,
                            rating = detail.ratingKinopoisk ?: seed.rating
                        )
                    } catch (e: Exception) {
                        Log.e("ActorRepository", "Ошибка загрузки фильма ${seed.id}: ${e.message}")
                        Film(
                            id = seed.id,
                            title = seed.title,
                            year = "",
                            posterUrl = null,
                            rating = seed.rating
                        )
                    }
                }
            }.awaitAll()
        }
    }

    override suspend fun getFilmsByProfession(actorId: Int, profession: Profession): List<Film> {
        val response = apiService.getActorDetails(actorId)
        return response.filmography.map { film ->
            Film(
                id = film.id,
                title = film.titleRu ?: film.titleEn ?: "Без названия",
                year = "",
                posterUrl = null,
                rating = film.rating?.toDoubleOrNull()
            )
        }
    }

    private data class FilmSeed(
        val id: Int,
        val title: String,
        val rating: Double?
    )
}