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
    suspend fun getMovie(id: Long): Movie
    suspend fun getFavoriteMovies(): List<Movie>
    suspend fun insertFavoriteMovie(movie: Movie)
    suspend fun getFavoriteMovie(id: Long): Movie
    suspend fun deleteFavoriteMovie(id: Long)
    suspend fun getLatestMovies(latestType: Int): List<Movie>
    suspend fun deleteNotFavoriteOrLatest(latestType: Int)
    suspend fun insertLatestMovies(movies: List<Movie>, latestType: Int)
}

class FavoriteMoviesRepository(private val movieDao: MovieDao): SavedMoviesRepository{
    override suspend fun getMovie(id: Long): Movie {
        return movieDao.getMovie(id)
    }
    override suspend fun getFavoriteMovies(): List<Movie> {
        return movieDao.getFavoriteMovies()
    }

    override suspend fun insertFavoriteMovie(movie: Movie) {
        val existingMovie = movieDao.getMovie(movie.id)
        if (existingMovie == null) {
            movie.isFavorite = true
            movieDao.insertMovie(movie)
        } else {
            movieDao.setToFavoriteMovie(movie.id)
        }
    }

    override suspend fun getFavoriteMovie(id: Long): Movie {
        return movieDao.getMovie(id)
    }

    override suspend fun deleteFavoriteMovie(id: Long) {
        movieDao.unFavoriteMovie(id)
        movieDao.deleteFavoriteMovie(id)
    }

    override suspend fun getLatestMovies(latestType: Int): List<Movie> {
        return movieDao.getLatestMovies(latestType)
    }

    override suspend fun deleteNotFavoriteOrLatest(latestType: Int) {
        movieDao.deleteNonFavoriteOrLatest(latestType)
    }

    override suspend fun insertLatestMovies(movies: List<Movie>, latestType: Int) {
        for (movie in movies) {
            val existingMovie = movieDao.getMovie(movie.id)
            if (existingMovie == null) {
                movie.latest = latestType
                movieDao.insertMovie(movie)
            } else {
                movieDao.setToLatestMovie(movie.id, latestType)
            }
        }
    }
}