package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Serializable
data class Movie(
    @SerialName(value = "id")
    var id: Long = 0L,

    @SerialName(value = "title")
    var title: String,

    @SerialName(value = "poster_path")
    var posterPath: String,

    @SerialName(value = "backdrop_path")
    var backdropPath: String,

    @SerialName(value = "release_date")
    var releaseDate: String,

    @SerialName(value = "overview")
    var overview: String,

    @SerialName(value = "genres")
    var genres: List<Genre> = listOf(),

    @SerialName(value = "homepage")
    var homeUrl: String = "",

    @SerialName(value = "imdb_id")
    var imdbId: String = ""
)
