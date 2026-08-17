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

    /**
     * Карточка фильма (постер, год, жанр, рейтинг) — фильмография таких данных не содержит,
     * поэтому список подгружает их по мере прокрутки.
     */
    suspend fun getFilmCard(filmId: Int): Film
}
