package com.example.skillcinema.domain.usecase

import com.example.skillcinema.data.Movie
import com.example.skillcinema.data.repository.MovieRepository

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    suspend fun execute(page: Int): List<Movie> {
        return repository.getTopMovies(page)
    }
}