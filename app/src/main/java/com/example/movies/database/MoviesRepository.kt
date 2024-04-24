package com.example.movies.database

import com.example.movies.model.Movie
import com.example.movies.model.MovieResponse
import com.example.movies.network.MovieDBApiService

interface MoviesRepository {
    suspend fun getPopularMovies(): MovieResponse
    suspend fun getTopRankedMovies(): MovieResponse
    suspend fun getMovieDetails(movie_id: String): Movie
}

class NetworkMoviesRepository(private val apiService: MovieDBApiService): MoviesRepository{
    override suspend fun getPopularMovies(): MovieResponse {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRankedMovies(): MovieResponse {
        return apiService.getTopRatedMovies()
    }
    override suspend fun getMovieDetails(movie_id: String): Movie{
        return apiService.getMovieDetails(movie_id)
    }
}