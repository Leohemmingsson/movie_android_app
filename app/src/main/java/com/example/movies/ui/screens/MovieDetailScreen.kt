package com.example.movies.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movies.R
import com.example.movies.model.Movie
import com.example.movies.ui.theme.MoviesTheme
import com.example.movies.utils.Constants
import com.example.movies.viewmodels.SelectedMovieUiState

@Composable
fun MovieDetailScreen(
    selectedMovieUiState: SelectedMovieUiState,
    modifier:Modifier = Modifier
) {
    when (selectedMovieUiState) {
        is SelectedMovieUiState.Success -> {
            Column {
                Box {
                    AsyncImage(
                        model = Constants.BACKDROP_IMAGE_BASE_URL + Constants.BACKDROP_IMAGE_WIDTH + selectedMovieUiState.movie.backdropPath,
                        contentDescription = selectedMovieUiState.movie.title,
                        modifier = modifier,
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(
                        text = selectedMovieUiState.movie.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    GenreRow(selectedMovieUiState.movie.genres)
                    Text(
                        text = selectedMovieUiState.movie.releaseDate,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = selectedMovieUiState.movie.overview,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Row {
                    LinkButton(
                        buttonText = R.string.Imdb_button,
                        url = Constants.IMDB_BASE_URL + selectedMovieUiState.movie.imdbId,
                    )
                    LinkButton(
                        buttonText = R.string.Home_page_button,
                        url = selectedMovieUiState.movie.homeUrl,
                    )
                }
            }
        }

        is SelectedMovieUiState.Loading -> {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }

        is SelectedMovieUiState.Error -> {
            Text(
                text = "Error...",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun LinkButton(
    @StringRes buttonText: Int,
    url: String
) {
    val context = LocalContext.current

    if (url != "") {
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
                context.startActivity(intent)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(stringResource(buttonText))
        }
    }
}

@Composable
fun GenreRow(genres: List<String>) {
    LazyRow {
        items(genres) { genre ->
            GenreBadge(genre = genre)
        }
    }
}

@Composable
fun GenreBadge(genre: String) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFd3d3d3), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text( text = genre )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMoreDetailedScreen() {
    MoviesTheme {
        MovieDetailScreen(
            selectedMovieUiState = SelectedMovieUiState.Success(
                Movie(
                    527774,
                    "Raya and the Last Dragon",
                    "/lPsD10PP4rgUGiGR4CCXA6iY0QQ.jpg",
                    "/9xeEGUZjgiKlI69jwIOi0hjKUIk.jpg",
                    "2021-03-03",
                    "Long ago, in the fantasy world of Kumandra, humans and dragons lived together in harmony. But when an evil force threatened the land, the dragons sacrificed themselves to save humanity. Now, 500 years later, that same evil has returned and it’s up to a lone warrior, Raya, to track down the legendary last dragon to restore the fractured land and its divided people.",
                    listOf<String>(
                        "Animation",
                        "Family",
                        "Fantasy",
                        "Action",
                        "Adventure"
                    ),
                    "https://movies.disney.com/raya-and-the-last-dragon",
                    "tt5109280"
                )),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
