package com.example.movies.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM favorite_movies WHERE id = :id")
    suspend fun getMovie(id: Long): Movie

    @Query("SELECT * FROM favorite_movies WHERE isFavorite = 1")
    suspend fun getFavoriteMovies(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movie)

    @Query("UPDATE favorite_movies SET isFavorite = 1 WHERE id = :id")
    suspend fun setToFavoriteMovie(id: Long)

    @Query("UPDATE favorite_movies SET latest = :latestType WHERE id = :id")
    suspend fun setToLatestMovie(id: Long, latestType: Int)

    @Query("UPDATE favorite_movies SET isFavorite = 0 WHERE id = :id")
    suspend fun unFavoriteMovie(id: Long)

    @Query("DELETE FROM favorite_movies WHERE id = :id AND latest = 0")
    suspend fun deleteFavoriteMovie(id: Long)

    @Query("SELECT * FROM favorite_movies WHERE latest == :latestType")
    suspend fun getLatestMovies(latestType: Int): List<Movie>

    @Query("UPDATE favorite_movies SET latest = 0")
    suspend fun setLatestToZero()

    @Query("DELETE FROM favorite_movies WHERE isFavorite = 0 AND latest != :latestType")
    suspend fun deleteNonFavoriteOrLatest(latestType: Int)

}