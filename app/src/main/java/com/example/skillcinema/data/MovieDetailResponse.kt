package com.example.skillcinema.data

import com.example.skillcinema.network.Country

data class MovieDetailResponse(
    val kinopoiskId: Int,
    val nameRu: String? = null,
    val nameOriginal: String?,
    val year: String?,
    val posterUrl: String?,
    val description: String?,
    val ratingKinopoisk: Double?,
    val genres: List<Genre>,
    val countries: List<Country>,
    val filmLength: Int?,
    val serial: Boolean,
    val seasonsCount: Int?,
    val ratingAgeLimits: String?
)

data class GalleryResponse(
    val total: Int,
    val items: List<GalleryItem>
)

data class GalleryItem(
    val imageUrl: String,
    val type: String
)

data class SeasonsResponse(
    val total: Int,
    val items: List<Season>
)

data class Season(
    val number: Int,
    val episodes: List<Episode>
)

data class Episode(
    val seasonNumber: Int,
    val episodeNumber: Int,
    val nameRu: String?,
    val nameEn: String?,
    val synopsis: String?,
    val releaseDate: String?
)

data class SimilarMoviesResponse(
    val items: List<Movie>
)