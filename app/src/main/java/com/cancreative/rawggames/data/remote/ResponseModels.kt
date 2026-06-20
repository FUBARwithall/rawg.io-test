package com.cancreative.rawggames.data.remote

import com.google.gson.annotations.SerializedName

// Start of Response Models

data class GamesResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<GameResponse>
)

data class GameResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("metacritic") val metacritic: Int?,
    @SerializedName("playtime") val playtime: Int?
)

data class GameDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description_raw") val descriptionRaw: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("released") val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("background_image_additional") val backgroundImageAdditional: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("metacritic") val metacritic: Int?,
    @SerializedName("playtime") val playtime: Int?,
    @SerializedName("developers") val developers: List<DeveloperResponse>?,
    @SerializedName("publishers") val publishers: List<PublisherResponse>?
)

data class DeveloperResponse(
    @SerializedName("name") val name: String
)

data class PublisherResponse(
    @SerializedName("name") val name: String
)

// End of Response Models
