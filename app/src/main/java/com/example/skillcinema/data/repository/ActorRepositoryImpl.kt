package com.example.skillcinema.data.repository

import android.util.Log
import com.example.skillcinema.data.Actor
import com.example.skillcinema.data.ActorDetailsResponse
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession
import com.example.skillcinema.domain.repositories.ActorRepository
import com.example.skillcinema.network.MovieApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ActorRepositoryImpl(
    private val apiService: MovieApiService
) : ActorRepository {

    // Экран актёра и экран фильмографии читают один и тот же ответ api/v1/staff/{id},
    // поэтому держим последний загруженный ответ, чтобы не ходить в сеть повторно.
    private val cacheMutex = Mutex()
    private var cachedActorId: Int? = null
    private var cachedResponse: ActorDetailsResponse? = null

    private suspend fun loadActor(actorId: Int): ActorDetailsResponse = cacheMutex.withLock {
        cachedResponse?.takeIf { cachedActorId == actorId }
            ?: withContext(Dispatchers.IO) { apiService.getActorDetails(actorId) }
                .also {
                    cachedActorId = actorId
                    cachedResponse = it
                }
    }

    override suspend fun getActorDetails(actorId: Int): Actor {
        val response = loadActor(actorId)
        return Actor(
            id = response.id,
            name = response.nameRu ?: response.nameEn ?: "Неизвестный актер",
            role = "",
            photoUrl = response.photoUrl,
            profession = response.profession
        )
    }

    override suspend fun getTopFilms(actorId: Int): List<Film> {
        val response = loadActor(actorId)

        val ratedFilms = response.filmography
            .distinctBy { it.id }
            .map { film -> film.toFilm() }
            .sortedByDescending { it.rating ?: Double.NEGATIVE_INFINITY }
            .take(TOP_FILMS_LIMIT)

        // Фильмография не содержит ни постера, ни года — дотягиваем их карточкой фильма,
        // чтобы элементы списка были такими же, как на вкладке «Главная».
        return withContext(Dispatchers.IO) {
            coroutineScope {
                ratedFilms.map { film ->
                    async {
                        try {
                            val detail = apiService.getMovieDetails(film.id)
                            film.copy(
                                title = detail.nameRu ?: detail.nameOriginal ?: film.title,
                                year = detail.year ?: "",
                                posterUrl = detail.posterUrlPreview ?: detail.posterUrl,
                                rating = detail.ratingKinopoisk ?: film.rating
                            )
                        } catch (e: Exception) {
                            Log.e("ActorRepository", "Ошибка загрузки фильма ${film.id}: ${e.message}")
                            film
                        }
                    }
                }.awaitAll()
            }
        }
    }

    override suspend fun getFilmography(actorId: Int): Map<Profession, List<Film>> {
        val response = loadActor(actorId)

        val grouped = response.filmography
            .groupBy { Profession.fromKey(it.professionKey) }
            .mapValues { (_, films) ->
                films
                    .distinctBy { it.id }
                    .map { it.toFilm() }
                    .sortedByDescending { it.rating ?: Double.NEGATIVE_INFINITY }
            }
            .filterValues { it.isNotEmpty() }

        // Табы должны идти в предсказуемом порядке: сначала актёрские роли, затем остальные.
        return Profession.entries
            .mapNotNull { profession -> grouped[profession]?.let { profession to it } }
            .toMap()
    }

    override suspend fun getFilmsByProfession(actorId: Int, profession: Profession): List<Film> =
        getFilmography(actorId)[profession].orEmpty()

    private fun ActorDetailsResponse.ActorFilm.toFilm() = Film(
        id = id,
        title = titleRu ?: titleEn ?: "Без названия",
        year = "",
        posterUrl = null,
        rating = rating?.toDoubleOrNull(),
        role = role?.takeIf { it.isNotBlank() }
    )

    private companion object {
        const val TOP_FILMS_LIMIT = 10
    }
}
