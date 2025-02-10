package com.example.skillcinema.domain.usecase

import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository

class GetMoviesByGenreAndCountryUseCase(private val repository: MovieRepository) {
    suspend fun execute(countryId: Int, genreId: Int): List<Movie> {
        return repository.getMoviesByGenreAndCountry(countryId, genreId)
    }
}