package com.example.movies.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movies.model.Movie
import com.example.movies.utils.Constants
import com.example.movies.viewmodels.MovieListUiState

@Composable
fun MovieGridScreen(
    movieListUiState : MovieListUiState,
    onMovieListItemClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier=modifier) {
        when (movieListUiState) {
            is MovieListUiState.Success -> {
                items(movieListUiState.movies) {movie ->
                    MovieGridItemCard(
                        movie = movie,
                        onMovieListItemClick = onMovieListItemClick,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            is MovieListUiState.Loading -> {
                item {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is MovieListUiState.Error -> {
                item {
                    Text(
                        text = "Error: Something went wrong!",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieGridItemCard(
    movie: Movie,
    onMovieListItemClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier
            .width(184.dp)
            .height(276.dp),
        onClick = {
            onMovieListItemClick(movie)
        }
    ){
        Column {
            Box (modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                AsyncImage(
                    model = Constants.POSTER_IMAGE_BASE_URL + Constants.POSTER_IMAGE_WIDTH + movie.posterPath,
                    contentDescription = movie.title,
                    modifier = modifier
                        .width(184.dp)
                        .height(276.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}

