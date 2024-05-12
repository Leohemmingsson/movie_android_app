package com.example.movies.database

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import com.example.movies.model.Movie
import com.example.movies.workers.GetDetailedMovieWorker
import com.example.movies.workers.GetMoviesWorker

interface GetMoviesRepository {
    val outputWorkInfo: Flow<WorkInfo?>
    fun getMovies(movieType: String)
    fun getDetailedMovie(movie: Movie)
    fun cancelWork()
}

class WorkManagerRepository(context: Context): GetMoviesRepository {
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo?> = MutableStateFlow(null)
    override fun getMovies(movieTypes: String) {
        val getMovieBuilder = OneTimeWorkRequestBuilder<GetMoviesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )

        getMovieBuilder.setInputData(createInputDataForWorkRequest(movieTypes))

        workManager.enqueue(getMovieBuilder.build())
    }

    override fun getDetailedMovie(movie: Movie) {
        val getMovieBuilder = OneTimeWorkRequestBuilder<GetDetailedMovieWorker>()

        getMovieBuilder.setInputData(createInputDataForDetailedMovieRequest(movie))

        workManager.enqueue(getMovieBuilder.build())
    }

    override fun cancelWork() {}

    private fun createInputDataForWorkRequest(movieType: String): Data {
        val builder = Data.Builder()
        builder.putString("MOVIE_TYPES", movieType.toString())
        return builder.build()
    }

    private fun createInputDataForDetailedMovieRequest(movie: Movie): Data {
        val builder = Data.Builder()
        builder.putString("MOVIE_ID", movie.id.toString())
        return builder.build()
    }
}