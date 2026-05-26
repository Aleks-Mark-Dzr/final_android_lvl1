package com.example.skillcinema.ui.search

enum class SearchType(val apiValue: String?) {
    ALL(null),
    FILMS("FILM"),
    TV_SERIES("TV_SERIES")
}

enum class SearchOrder(val apiValue: String) {
    RATING("RATING"),
    NUM_VOTE("NUM_VOTE"),
    YEAR("YEAR")
}

data class SearchSettings(
    val type: SearchType = SearchType.ALL,
    val countryId: Int? = null,
    val genreId: Int? = null,
    val yearFrom: Int? = 1900,
    val yearTo: Int? = 2030,
    val ratingFrom: Int? = 1,
    val ratingTo: Int? = 10,
    val order: SearchOrder = SearchOrder.RATING,
    val hideViewed: Boolean = false
)