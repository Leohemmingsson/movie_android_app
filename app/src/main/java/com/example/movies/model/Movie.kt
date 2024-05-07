package com.example.movies.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)

@Serializable
@Entity(tableName = "favorite_movies")
data class Movie(
    @PrimaryKey
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
    var imdbId: String = "",

    var isFavorite: Boolean = false,

    var latest: Int  = 0
)
