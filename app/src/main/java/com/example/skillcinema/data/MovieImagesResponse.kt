package com.example.skillcinema.data

/**
 * Представляет ответ API с изображениями фильма.
 * Запросы выполняются через эндпоинт `/api/v2.2/films/{id}/images`.
 */
data class MovieImagesResponse(
    val total: Int,
    val totalPages: Int? = null,
    val items: List<MovieImage>
)

/**
 * Описание отдельного изображения фильма.
 */
data class MovieImage(
    val imageUrl: String,
    val previewUrl: String?
)