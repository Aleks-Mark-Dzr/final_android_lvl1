package com.example.skillcinema.domain.models

data class Film(
    val id: Int,
    val title: String,
    val year: String,
    val posterUrl: String?,
    val rating: Double?
)