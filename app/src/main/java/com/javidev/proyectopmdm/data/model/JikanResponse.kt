package com.javidev.proyectopmdm.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class JikanResponse(
    val data: List<Anime>
)

@Parcelize
data class Anime(
    val mal_id: Int,
    val title: String,
    @Json(name = "images") val images: AnimeImages,
    val episodes: Int?,
    val score: Double?,
    val synopsis: String?
) : Parcelable

@Parcelize
data class AnimeImages(
    @Json(name = "jpg") val jpg: ImageUrl
) : Parcelable

@Parcelize
data class ImageUrl(
    @Json(name = "image_url") val imageUrl: String
) : Parcelable
