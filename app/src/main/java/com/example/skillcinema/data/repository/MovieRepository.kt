package com.example.skillcinema.data.repository

import com.example.skillcinema.data.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getTopMovies(page: Int): List<Movie>
}