package com.example.movies.model

data class Movie(
    var id: Long = 0L,
    var title: String,
    var posterPath: String,
    var backdropPath: String,
    var releaseDate: String,
    var overview: String,
    var genres: List<String>,
    var homeUrl: String,
    var imdbId: String
)
