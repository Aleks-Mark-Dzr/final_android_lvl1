package com.example.skillcinema.domain.repositories

import com.example.skillcinema.data.Actor
//import com.example.skillcinema.domain.models.Actor
import com.example.skillcinema.domain.models.Film
import com.example.skillcinema.domain.models.Profession

interface ActorRepository {
    suspend fun getActorDetails(actorId: Int): Actor
    suspend fun getTopFilms(actorId: Int): List<Film>
    suspend fun getFilmsByProfession(actorId: Int, profession: Profession): List<Film>
}