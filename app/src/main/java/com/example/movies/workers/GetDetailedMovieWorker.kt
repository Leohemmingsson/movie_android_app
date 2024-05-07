package com.example.movies.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.movies.R
import com.example.movies.database.WorkManagerRepository
import com.example.movies.viewmodels.MovieDBViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "GetDetailedMovieWorker"

class GetDetailedMovieWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {

        return withContext(Dispatchers.IO) {

            return@withContext try {

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
