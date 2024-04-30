package com.example.movies.network

import com.example.movies.model.Movie
import com.example.movies.model.MovieListResponse
import com.example.movies.model.MovieVideoResponse
import com.example.movies.model.ReviewListResponse
import com.example.movies.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDBApiService {
    @GET("popular")
    suspend fun getPopularMovies (
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieListResponse

    @GET("top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieListResponse

    @GET("{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id")
        movie_id: String,
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): Movie

    @GET("{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id")
        movie_id: String,
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): ReviewListResponse

    @GET("{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id")
        movie_id: String,
        @Query("api_key")
        apiKey: String = Constants.API_KEY
    ): MovieVideoResponse

}
