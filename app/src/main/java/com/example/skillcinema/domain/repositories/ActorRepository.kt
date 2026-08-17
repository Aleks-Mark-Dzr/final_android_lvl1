package com.example.skillcinema.domain.repositories

import com.example.skillcinema.data.Actor
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession

interface ActorRepository {
    suspend fun getActorDetails(actorId: Int): Actor

    /** До десяти фильмов актёра с наибольшим рейтингом (с постерами и годом). */
    suspend fun getTopFilms(actorId: Int): List<Film>

    /** Вся фильмография, сгруппированная по профессиям; порядок ключей — как в [Profession]. */
    suspend fun getFilmography(actorId: Int): Map<Profession, List<Film>>

    suspend fun getFilmsByProfession(actorId: Int, profession: Profession): List<Film>
}
