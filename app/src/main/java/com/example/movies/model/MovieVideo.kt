package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieVideo(
    @SerialName("name")
    val name: String,

    @SerialName("key")
    val key: String,

    @SerialName("site")
    val site: String,

    @SerialName("published_at")
    val publishedAt: String,

    @SerialName("id")
    val id: String
)