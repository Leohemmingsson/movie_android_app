package com.example.movies.network

import com.example.movies.model.Movie
import com.example.movies.model.MovieResponse
import com.example.movies.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDBApiService {
    @GET("popular")
    suspend fun getPopularMovies (
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieResponse

    @GET("top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieResponse

    @GET("{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id")
        movie_id: String,
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): Movie
}