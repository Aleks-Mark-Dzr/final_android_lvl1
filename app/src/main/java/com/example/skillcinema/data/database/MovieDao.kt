package com.example.skillcinema.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Query("UPDATE movies SET isFavorite = :favorite WHERE movieId = :movieId")
    suspend fun updateFavoriteStatus(movieId: Int, favorite: Boolean)

    @Query("UPDATE movies SET inCollections = :inCollections WHERE movieId = :movieId")
    suspend fun updateInCollections(movieId: Int, inCollections: String)

    @Query("UPDATE movies SET isWatched = :watched WHERE movieId = :movieId")
    suspend fun updateWatchedStatus(movieId: Int, watched: Boolean)

    @Query("UPDATE movies SET isWatchLater = :watchLater WHERE movieId = :movieId")
    suspend fun updateWatchLaterStatus(movieId: Int, watchLater: Boolean)

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isWatchLater = 1")
    fun getWatchLaterMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isWatched = 1 ORDER BY movieId DESC")
    fun getWatchedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE ',' || inCollections || ',' LIKE '%,' || :collectionId || ',%'")
    fun getMoviesInCollection(collectionId: Int): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>
}