package com.example.skillcinema.network

data class GenresAndCountriesResponse(
    val genres: List<Genre>,
    val countries: List<Country>
)

data class Genre(val id: Int, val genre: String)
data class Country(val id: Int, val country: String)