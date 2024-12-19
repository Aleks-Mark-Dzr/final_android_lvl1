package com.example.skillcinema.data

data class TopMoviesResponse(
    val films: List<Movie>
)

data class Movie(
    val filmId: Int,
    val nameRu: String,
    val year: String,
    val posterUrlPreview: String
)