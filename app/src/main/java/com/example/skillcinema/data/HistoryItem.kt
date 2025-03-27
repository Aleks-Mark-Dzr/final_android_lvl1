package com.example.skillcinema.data

data class HistoryItem(
    val id: Int,
    val title: String,
    val type: ItemType,
    val imageUrl: String? = null
)

enum class ItemType {
    MOVIE,
    SERIES,
    PERSON
}