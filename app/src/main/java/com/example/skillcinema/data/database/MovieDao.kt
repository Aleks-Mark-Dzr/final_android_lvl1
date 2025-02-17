package com.example.skillcinema.data.database

import androidx.room.*
import com.example.skillcinema.data.database.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Query("UPDATE movies SET isFavorite = :favorite WHERE movieId = :movieId")
    suspend fun updateFavoriteStatus(movieId: Int, favorite: Boolean)

    @Query("UPDATE movies SET isWatched = :watched WHERE movieId = :movieId")
    suspend fun updateWatchedStatus(movieId: Int, watched: Boolean)
}