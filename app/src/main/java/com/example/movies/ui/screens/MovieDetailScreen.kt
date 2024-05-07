package com.example.movies.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.movies.R
import com.example.movies.model.Genre
import com.example.movies.model.MovieVideo
import com.example.movies.model.Review
import com.example.movies.utils.Constants
import com.example.movies.viewmodels.MovieDBViewModel
import com.example.movies.viewmodels.SelectedMovieUiState

@Composable
fun MovieDetailScreen(
    movieDBViewModel: MovieDBViewModel,
    modifier:Modifier = Modifier
) {
    val selectedMovieUiState = movieDBViewModel.selectedMovieUiState

    when (selectedMovieUiState) {
        is SelectedMovieUiState.Success -> {

            Column (
                modifier = modifier.verticalScroll(rememberScrollState())
            ){
                Box {
                    AsyncImage(
                        model = Constants.BACKDROP_IMAGE_BASE_URL + Constants.BACKDROP_IMAGE_WIDTH + selectedMovieUiState.movie.backdropPath,
                        contentDescription = selectedMovieUiState.movie.title,
                        modifier = Modifier.fillMaxWidth(),
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
                Spacer(modifier = Modifier.size(8.dp))
                Row {
                    Text(
                        text = "Favorite",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(checked = selectedMovieUiState.isFavorite, onCheckedChange = {
                        if (it) {
                            movieDBViewModel.saveFavoriteMovie(selectedMovieUiState)
                        } else {
                            movieDBViewModel.deleteFavoriteMovie(selectedMovieUiState)
                        }
                    })
                }
                Spacer(modifier = Modifier.size(8.dp))
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
                Column {
                    ReviewHorizontal(selectedMovieUiState.reviews)
                }
                Spacer(modifier = Modifier.size(16.dp))

//                VideoHorizontal(selectedMovieUiState.videos)
                Spacer(modifier = Modifier.size(16.dp))
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
fun GenreRow(genres: List<Genre>) {
    LazyRow {
        items(genres) { genre ->
            GenreBadge(genre = genre.name)
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
        Text(
            text = genre,
            color = Color.Black
            )
    }
}

@Composable
fun ReviewHorizontal(reviews: List<Review>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(reviews) { review ->
            ReviewCard(review)
        }
    }
}

@Composable
fun ReviewCard(review: Review, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) } // State to track if the card is expanded

    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(300.dp)
            .clickable { isExpanded = !isExpanded } // Toggle expand on click
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Author: ${review.author.name}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Rating: ${review.author.rating}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            AnimatedVisibility(visible = isExpanded) { // Only show this if expanded
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(max = 200.dp) // Limiting the maximum height
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = review.content,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun VideoHorizontal(video_uris: List<MovieVideo>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp) // Padding around the LazyRow content
    ) {
        items(video_uris) { video ->
            var video_uri = Constants.YOUTUBE_BASE_URL + video.key
            ExoPlayerView(video_uri, Modifier.padding(horizontal = 8.dp)) // Padding between items
        }
    }
}



@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView(video_uri: String, modifier: Modifier = Modifier) {
    // Get the current context
    val context = LocalContext.current

    // Initialize ExoPlayer
    val exoPlayer = ExoPlayer.Builder(context).build()

    // Create a MediaSource
    val mediaSource = remember(video_uri) {
        MediaItem.fromUri(video_uri)
    }

    // Set MediaSource to ExoPlayer
    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
    }

    // Manage lifecycle events
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Use AndroidView to embed an Android View (PlayerView) into Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // Set your desired height
    )
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewMoreDetailedScreen() {
//    MoviesTheme {
//        MovieDetailScreen(
//            selectedMovieUiState = SelectedMovieUiState.Success(
//                movie = Movie(
//                    527774,
//                    "Raya and the Last Dragon",
//                    "/lPsD10PP4rgUGiGR4CCXA6iY0QQ.jpg",
//                    "/9xeEGUZjgiKlI69jwIOi0hjKUIk.jpg",
//                    "2021-03-03",
//                    "Long ago, in the fantasy world of Kumandra, humans and dragons lived together in harmony. But when an evil force threatened the land, the dragons sacrificed themselves to save humanity. Now, 500 years later, that same evil has returned and it’s up to a lone warrior, Raya, to track down the legendary last dragon to restore the fractured land and its divided people.",
//                    listOf<Genre>(
//                        Genre(0, "Animation"),
//                        Genre(1,"Family")
//                    ),
//                    "https://movies.disney.com/raya-and-the-last-dragon",
//                    "tt5109280"
//                )),
//            modifier = Modifier
//                .fillMaxSize()
//        )
//    }
//}
