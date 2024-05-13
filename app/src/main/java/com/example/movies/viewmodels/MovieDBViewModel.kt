package com.example.movies.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.impl.workers.ConstraintTrackingWorker
import com.example.movies.MovieDBApplication
import com.example.movies.database.MoviesRepository
import com.example.movies.database.SavedMoviesRepository
import com.example.movies.database.WorkManagerRepository
import com.example.movies.model.Movie
import com.example.movies.model.MovieVideo
import com.example.movies.model.Review
import com.example.movies.network.NetworkHandler
import com.example.movies.utils.isNetworkAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MovieListUiState {
    data class Success(val movies: List<Movie>) : MovieListUiState
    object Error : MovieListUiState
    object Loading : MovieListUiState
}

sealed interface SelectedMovieUiState {
    data class Success(val movie: Movie, val reviews: List<Review>, val videos: List<MovieVideo>, val isFavorite: Boolean = false) : SelectedMovieUiState
    object Error : SelectedMovieUiState
    object Loading : SelectedMovieUiState
}


class MovieDBViewModel(
//    private val moviesRepository: MoviesRepository,
    private val savedMoviesRepository: SavedMoviesRepository,
    private val workerManagerRepository: WorkManagerRepository,
    private val networkHandler: NetworkHandler
): ViewModel() {
    var movieListUiState: MovieListUiState by mutableStateOf(MovieListUiState.Loading)
        private set
    var selectedMovieUiState: SelectedMovieUiState by mutableStateOf(SelectedMovieUiState.Loading)
        private set

    init {
//        getPopularMovies()
        networkHandler.getNetworkLiveData().observeForever { isAvailable ->
            if (isAvailable) {
                reloadMovies()
            }
        }
//        observeNetworkChanges()
    }

private fun reloadMovies() {
    getPopularMovies()
    getTopRatedMovies()
    // Additional data reload methods
}

override fun onCleared() {
    super.onCleared()
    networkHandler.unregisterNetworkCallback()
}


//    private fun observeNetworkChanges() {
//        networkHandler.getNetworkLiveData().observe(this, { isConnected ->
//            if (isConnected) {
//                // Code to execute when there is an Internet connection
//                handleConnectionAvailable()
//            } else {
//                // Code to execute when there is no Internet connection
//                handleConnectionLost()
//            }
//        })
//    }

    fun getPopularMovies() {
        viewModelScope.launch {
          movieListUiState = MovieListUiState.Loading

            savedMoviesRepository.deleteNotFavoriteOrLatest(1)

            workerManagerRepository.getMovies("popular")

            delay(2000L)

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
            movieListUiState = MovieListUiState.Loading


            savedMoviesRepository.deleteNotFavoriteOrLatest(2)

            workerManagerRepository.getMovies("top_ranked")

            delay(2000L)

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
                val movieId = selectedMovie.id

                if (movieId != null) {
//                    val movie: Movie = moviesRepository.getMovieDetails(movieId)
//                    val reviews: List<Review> = moviesRepository.getMovieReviews(movieId).results
//                    val videos: List<MovieVideo> = moviesRepository.getMovieVideos(movieId).results
                    val movie = savedMoviesRepository.getMovie(movieId)
                    setSelectedMovie(movie = movie)
//                    setSelectedMovie(movie = movie, reviews = reviews, videos = videos)
                } else {
                    SelectedMovieUiState.Error
                }
        }
    }

    fun getSavedMovies() {
        viewModelScope.launch {
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


    fun setErrorView() {
        viewModelScope.launch {
            MovieListUiState.Error
        }
    }
    fun setLoadingView() {
        viewModelScope.launch {
            MovieListUiState.Loading
        }
    }

    fun setListView(movies: List<Movie>) {
        viewModelScope.launch {
            MovieListUiState.Success(movies)
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

    fun setSelectedMovie(movie: Movie, reviews: List<Review> = listOf(), videos: List<MovieVideo> = listOf()) {
        viewModelScope.launch {
            selectedMovieUiState = SelectedMovieUiState.Loading
            selectedMovieUiState = try {
                SelectedMovieUiState.Success(movie = movie, reviews = reviews, videos = videos, savedMoviesRepository.getFavoriteMovie(movie.id)?.isFavorite ?: false)
            } catch (e: IOException) {
                SelectedMovieUiState.Error
            } catch (e: HttpException) {
                SelectedMovieUiState.Error
            }
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovieDBApplication)
//                val moviesRepository = application.container.moviesRepository
                val savedMovieRepository = application.container.savedMoviesRepository
                val workerManagerRepository = application.container.workerManagerRepository
                val networkHandler = application.container.networkHandler
                MovieDBViewModel(savedMoviesRepository = savedMovieRepository, workerManagerRepository = workerManagerRepository, networkHandler = networkHandler)
            }
        }
    }

}