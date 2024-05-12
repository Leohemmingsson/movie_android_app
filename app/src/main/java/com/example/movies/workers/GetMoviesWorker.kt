package com.example.movies.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.movies.R
import com.example.movies.database.DefaultAppContainer
import com.example.movies.database.MovieDao
import com.example.movies.database.MovieDatabase
import com.example.movies.model.MovieListResponse
import com.example.movies.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "GetMoviesWorker"
class GetMoviesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val movieDao: MovieDao = MovieDatabase.getDatabase(ctx).movieDao()
    private val appContainer: DefaultAppContainer = DefaultAppContainer(ctx)


    override suspend fun doWork(): Result {
        if (!isNetworkAvailable(applicationContext)) {
            return Result.retry()
        }
        val typeOfList = inputData.getString("MOVIE_TYPES")
        println("[DEBUG] typeOfList: $typeOfList")

        var movies_response: MovieListResponse? = null

        when (typeOfList) {
            "popular" -> {
                movies_response = appContainer.moviesRepository.getPopularMovies()
                appContainer.savedMoviesRepository.insertLatestMovies(movies_response.results, 1)
            }
            "top_ranked" -> {
                movies_response = appContainer.moviesRepository.getTopRankedMovies()
                appContainer.savedMoviesRepository.insertLatestMovies(movies_response.results, 2)
            }
            else -> {
                Log.e(TAG, "Invalid movie type")
            }
        }
        println("[DEBUG] movies: ${movies_response?.results}")

        return Result.success()
    }
}
