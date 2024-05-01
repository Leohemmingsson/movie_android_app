package com.example.movies.database

import com.example.movies.model.Movie
import com.example.movies.model.MovieListResponse
import com.example.movies.model.MovieVideoResponse
import com.example.movies.model.ReviewListResponse
import com.example.movies.network.MovieDBApiService

interface MoviesRepository {
    suspend fun getPopularMovies(): MovieListResponse
    suspend fun getTopRankedMovies(): MovieListResponse
    suspend fun getMovieDetails(movie_id: String): Movie
    suspend fun getMovieReviews(movie_id: String): ReviewListResponse
    suspend fun getMovieVideos(movie_id: String): MovieVideoResponse
}

class NetworkMoviesRepository(private val apiService: MovieDBApiService): MoviesRepository{
    override suspend fun getPopularMovies(): MovieListResponse {
        return apiService.getPopularMovies()
    }

    override suspend fun getTopRankedMovies(): MovieListResponse {
        return apiService.getTopRatedMovies()
    }
    override suspend fun getMovieDetails(movie_id: String): Movie{
        return apiService.getMovieDetails(movie_id)
    }

    override suspend fun getMovieReviews(movie_id: String): ReviewListResponse{
        return apiService.getMovieReviews(movie_id)
    }

    override suspend fun getMovieVideos(movie_id: String): MovieVideoResponse{
        return apiService.getMovieVideos(movie_id)
    }
}

interface SavedMoviesRepository {
    suspend fun getSavedMovies(): List<Movie>
    suspend fun insertMovie(movie: Movie)
    suspend fun getMovie(id: Long): Movie
    suspend fun deleteMovie(id: Long)
}

class FavoriteMoviesRepository(private val movieDao: MovieDao): SavedMoviesRepository{
    override suspend fun getSavedMovies(): List<Movie> {
        return movieDao.getFavoriteMovies()
    }

    override suspend fun insertMovie(movie: Movie) {
        movieDao.insertFavoriteMovie(movie)
    }

    override suspend fun getMovie(id: Long): Movie {
        return movieDao.getMovie(id)
    }

    override suspend fun deleteMovie(id: Long) {
        movieDao.deleteFavoriteMovie(id)
    }
}