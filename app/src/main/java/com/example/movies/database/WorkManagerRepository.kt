package com.example.movies.database

import android.content.Context
import androidx.work.Data
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
    override fun getMovies(movieType: String) {
        // Add WorkRequest to blur the image
        val getMovieBuilder = OneTimeWorkRequestBuilder<GetMoviesWorker>()

        // Input the Uri for the blur operation along with the blur level
        getMovieBuilder.setInputData(createInputDataForWorkRequest(movieType))

        workManager.enqueue(getMovieBuilder.build())
    }

    override fun getDetailedMovie(movie: Movie) {
        // Add WorkRequest to blur the image
        val getMovieBuilder = OneTimeWorkRequestBuilder<GetDetailedMovieWorker>()

        // Input the Uri for the blur operation along with the blur level
        getMovieBuilder.setInputData(createInputDataForDetailedMovieRequest(movie))

        workManager.enqueue(getMovieBuilder.build())
    }

    override fun cancelWork() {}

    private fun createInputDataForWorkRequest(movieType: String): Data {
        val builder = Data.Builder()
        builder.putString("MOVIE_TYPE", movieType.toString())
        return builder.build()
    }

    private fun createInputDataForDetailedMovieRequest(movie: Movie): Data {
        val builder = Data.Builder()
        builder.putString("MOVIE_ID", movie.id.toString())
        return builder.build()
    }
}