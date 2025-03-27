package com.example.skillcinema.data

data class Collection(
    val id: Int,
    val name: String,
    val items: List<Movie>
)