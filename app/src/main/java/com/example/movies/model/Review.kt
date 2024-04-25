package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AuthorDetails(
    @SerialName(value = "name")
    var name: String,

    @SerialName(value = "avatar_path")
    var avatarPath: String?,

    @SerialName(value = "rating")
    var rating: Double
)

@Serializable
data class Review(
    @SerialName(value = "author_details")
    var author: AuthorDetails,

    @SerialName(value = "content")
    var content: String,

    @SerialName(value = "created_at")
    var createdAt: String
)
