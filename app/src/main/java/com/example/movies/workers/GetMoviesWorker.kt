package com.example.movies.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.movies.R
import com.example.movies.database.DefaultAppContainer
import com.example.movies.database.MovieDao
import com.example.movies.database.MovieDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "GetMoviesWorker"
class GetMoviesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    private val movieDao: MovieDao = MovieDatabase.getDatabase(ctx).movieDao()
    private val appContainer: DefaultAppContainer = DefaultAppContainer(ctx)


    override suspend fun doWork(): Result {
        val test = appContainer.moviesRepository.getPopularMovies()

        return withContext(Dispatchers.IO) {

            return@withContext try {
                TODO()
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_getting_movies),
                    throwable
                )
                Result.failure()
            }
        }
    }
}
