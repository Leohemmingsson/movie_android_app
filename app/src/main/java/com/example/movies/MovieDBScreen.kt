package com.example.movies

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movies.model.Genre
import com.example.movies.model.Movie
import com.example.movies.ui.screens.MovieDetailScreen
import com.example.movies.ui.screens.MovieGridScreen
import com.example.movies.ui.screens.MovieListScreen
import com.example.movies.viewmodels.MovieDBViewModel
import com.example.movies.viewmodels.MovieListUiState
import com.example.movies.viewmodels.SelectedMovieUiState

enum class MovieDBScreen(@StringRes val title: Int) {
    List(title = R.string.app_name),
    Detail(title = R.string.movie_details),

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDBAppBar(
    currentScreen: MovieDBScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    movieDBViewModel: MovieDBViewModel
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        actions = {
            if (currentScreen == MovieDBScreen.List) {
                IconButton(onClick = {
                    menuExpanded = !menuExpanded
                }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Open menu to select different movie lists"
                    )

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.popular_movies)) },
                            onClick = {
                                movieDBViewModel.getPopularMovies()

                                menuExpanded = false
                            })

                        DropdownMenuItem(text = {
                            Text(stringResource(R.string.top_rated_movies))
                        }, onClick = {
                            movieDBViewModel.getTopRatedMovies()

                            menuExpanded = false
                        })

                        DropdownMenuItem(text = {
                            Text(stringResource(R.string.saved_movies))
                        }, onClick = {
                            movieDBViewModel.getSavedMovies()

                            menuExpanded = false
                        })
                    }

                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }

    )
}


@Composable
fun TheMovieDBApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = MovieDBScreen.valueOf(
        backStackEntry?.destination?.route ?: MovieDBScreen.List.name
    )

    val movieDBViewModel : MovieDBViewModel = viewModel(factory = MovieDBViewModel.Factory)

    Scaffold (
        topBar = {
            MovieDBAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                movieDBViewModel = movieDBViewModel
            )
        }
    ) {innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MovieDBScreen.List.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = MovieDBScreen.List.name) {
                    // MovieListScreen(
                    MovieGridScreen(
                        movieListUiState = movieDBViewModel.movieListUiState,
                        onMovieListItemClick = {movie ->

                            movieDBViewModel.getMovieDetails(movie)
                            navController.navigate(MovieDBScreen.Detail.name)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
            }

            composable(route = MovieDBScreen.Detail.name) {
                MovieDetailScreen(
                    movieDBViewModel = movieDBViewModel,
                    modifier = Modifier
                )
            }


        }
    }
}