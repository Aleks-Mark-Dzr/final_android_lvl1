package com.example.skillcinema.data

data class Movie(
    val filmId: Int,
    val kinopoiskId: Int,
    val nameRu: String,
    val year: String,
    val posterUrlPreview: String,
    val rating: Double?,
    val ratingKinopoisk: Double?,
    val genres: List<Genre>
)


data class Genre(
    val genre: String
)