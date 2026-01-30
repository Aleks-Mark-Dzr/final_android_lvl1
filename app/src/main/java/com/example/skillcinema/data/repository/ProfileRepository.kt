package com.example.skillcinema.data.repository

import android.content.SharedPreferences
import com.example.skillcinema.data.Collection
import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.database.MovieDao
import com.example.skillcinema.data.database.MovieEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ProfileRepository {
    fun getFavoriteMovies(): Flow<List<Movie>>
    fun loadCustomCollections(): List<Collection>
    fun saveCustomCollections(collections: List<Collection>)
}

class ProfileRepositoryImpl(
    private val movieDao: MovieDao,
    private val preferences: SharedPreferences
) : ProfileRepository {

    private val gson = Gson()

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDao.getFavoriteMovies().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    override fun loadCustomCollections(): List<Collection> {
        val json = preferences.getString(KEY_CUSTOM_COLLECTIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<StoredCollection>>() {}.type
        val stored = runCatching { gson.fromJson<List<StoredCollection>>(json, type) }
            .getOrDefault(emptyList())
        return stored.map { Collection(it.id, it.name, emptyList()) }
    }

    override fun saveCustomCollections(collections: List<Collection>) {
        if (collections.isEmpty()) {
            preferences.edit().remove(KEY_CUSTOM_COLLECTIONS).apply()
            return
        }
        val payload = collections.map { StoredCollection(it.id, it.name) }
        preferences.edit().putString(KEY_CUSTOM_COLLECTIONS, gson.toJson(payload)).apply()
    }

    private fun MovieEntity.toMovie(): Movie {
        return Movie(
            filmId = movieId,
            kinopoiskId = movieId,
            nameRu = nameRu,
            year = year ?: "",
            posterUrlPreview = posterUrl ?: "",
            rating = rating,
            ratingKinopoisk = rating,
            genres = emptyList()
        )
    }

    private data class StoredCollection(
        val id: Int,
        val name: String
    )

    private companion object {
        private const val KEY_CUSTOM_COLLECTIONS = "custom_collections"
    }
}