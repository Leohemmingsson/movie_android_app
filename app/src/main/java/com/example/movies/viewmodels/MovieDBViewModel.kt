package com.example.movies.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.impl.workers.ConstraintTrackingWorker
import com.example.movies.MovieDBApplication
import com.example.movies.database.FavoriteMoviesRepository
import com.example.movies.database.MoviesRepository
import com.example.movies.database.SavedMoviesRepository
import com.example.movies.database.WorkManagerRepository
import com.example.movies.model.Movie
import com.example.movies.model.MovieVideo
import com.example.movies.model.Review
import com.example.movies.network.NetworkHandler
import com.example.movies.utils.isNetworkAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>) : MovieListUiState
    object Error : MovieListUiState
    object Loading : MovieListUiState
    object NoInternet: MovieListUiState
}

sealed interface SelectedMovieUiState {
    data class Success(val movie: Movie, val reviews: List<Review>, val videos: List<MovieVideo>, val isFavorite: Boolean = false) : SelectedMovieUiState
    object Error : SelectedMovieUiState
    object Loading : SelectedMovieUiState
    object NoInternet: SelectedMovieUiState
}


class MovieDBViewModel(
    private val moviesRepository: MoviesRepository,
    private val savedMoviesRepository: SavedMoviesRepository,
    private val workerManagerRepository: WorkManagerRepository,
    private val networkHandler: NetworkHandler
): ViewModel() {
    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set
    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    var currentMovieView by mutableStateOf<Int>(1)
        private set

    init {
        println("[DEBUG] In MovieDBViewModel init")
        networkHandler.getNetworkLiveData().observeForever { isAvailable ->
            if (isAvailable) {
                println("[DEBUG] Network is available!")
                reloadMovies()
            } else {
                selectedMovieUiState = SelectedMovieUiState.NoInternet
                viewModelScope.launch {
                    val isSaved = savedMoviesRepository.getLatestMovies(currentMovieView) != emptyArray<Movie>()
                    if (isSaved) {
                        loadMovies()
                    } else {
                        setNoInternet()
                    }
                }
            }
        }
    }


private fun loadMovies() {
    when (currentMovieView) {
        1 -> {
            getPopularMovies()
        }
        2 -> {
            getTopRatedMovies()
        }
        3 -> {
            getSavedMovies()
        }
    }
}
private fun reloadMovies() {
    when (currentMovieView) {
        1 -> {
            workerManagerRepository.getMovies("popular")
            getPopularMovies()
        }
        2 -> {
            workerManagerRepository.getMovies("top_ranked")
            getTopRatedMovies()
        }
        3 -> {
            getSavedMovies()
        }
    }
}

override fun onCleared() {
    super.onCleared()
    networkHandler.unregisterNetworkCallback()
}

    fun getPopularMovies() {
        viewModelScope.launch {
            println("[DEBUG] In getPopularMovies")
            currentMovieView = 1
            movieListUiState = MovieListUiState.Loading

            savedMoviesRepository.deleteNotFavoriteOrLatest(1)

            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getLatestMovies(1))
//                MovieListUiState.Success(moviesRepository.getPopularMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            currentMovieView = 2
            movieListUiState = MovieListUiState.Loading

            savedMoviesRepository.deleteNotFavoriteOrLatest(2)

            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getLatestMovies(2))
//                MovieListUiState.Success(moviesRepository.getTopRankedMovies().results)
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }

    fun getMovieDetails(selectedMovie: Movie) {
        viewModelScope.launch {
            networkHandler.getNetworkLiveData().asFlow().first().let { isAvailable ->
                if (!isAvailable) {
                    selectedMovieUiState = SelectedMovieUiState.NoInternet
                    return@launch
                }
            }

            val movieId = selectedMovie.id.toString()

                if (movieId != null) {
                    val movie: Movie = moviesRepository.getMovieDetails(movieId)
                    val reviews: List<Review> = moviesRepository.getMovieReviews(movieId).results
                    val videos: List<MovieVideo> = moviesRepository.getMovieVideos(movieId).results
                    setSelectedMovie(movie = movie, reviews = reviews, videos = videos)
                } else {
                    SelectedMovieUiState.Error
                }
        }
    }

    fun getSavedMovies() {
        viewModelScope.launch {
            currentMovieView = 3
            movieListUiState = MovieListUiState.Loading
            movieListUiState = try {
                MovieListUiState.Success(savedMoviesRepository.getFavoriteMovies())
            } catch (e: IOException) {
                MovieListUiState.Error
            } catch (e: HttpException) {
                MovieListUiState.Error
            }
        }
    }


    fun setNoInternet() {
        viewModelScope.launch {
            movieListUiState = MovieListUiState.NoInternet
        }
    }
    fun saveFavoriteMovie(selectedState: SelectedMovieUiState.Success) {
        viewModelScope.launch {
            savedMoviesRepository.insertFavoriteMovie(selectedState.movie)
            selectedMovieUiState = SelectedMovieUiState.Success(selectedState.movie, selectedState.reviews, selectedState.videos, true)
        }
    }

    fun deleteFavoriteMovie(selectedState: SelectedMovieUiState.Success) {
        viewModelScope.launch {
            savedMoviesRepository.deleteFavoriteMovie(selectedState.movie.id)
            selectedMovieUiState = SelectedMovieUiState.Success(selectedState.movie, selectedState.reviews, selectedState.videos, false)
        }
    }

    fun setSelectedMovie(movie: Movie?, reviews: List<Review> = listOf(), videos: List<MovieVideo> = listOf()) {
        viewModelScope.launch {
            if (movie == null) {
                selectedMovieUiState = SelectedMovieUiState.NoInternet
            } else {
                selectedMovieUiState = SelectedMovieUiState.Loading
                selectedMovieUiState = try {
                    SelectedMovieUiState.Success(
                        movie = movie,
                        reviews = reviews,
                        videos = videos,
                        savedMoviesRepository.getFavoriteMovie(movie.id)?.isFavorite ?: false
                    )
                } catch (e: IOException) {
                    SelectedMovieUiState.Error
                } catch (e: HttpException) {
                    SelectedMovieUiState.Error
                }
            }
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
                val moviesRepository = application.container.moviesRepository
                val savedMovieRepository = application.container.savedMoviesRepository
                val workerManagerRepository = application.container.workerManagerRepository
                val networkHandler = application.container.networkHandler
                MovieDBViewModel(moviesRepository = moviesRepository, savedMoviesRepository = savedMovieRepository, workerManagerRepository = workerManagerRepository, networkHandler = networkHandler)
            }
        }
    }

}