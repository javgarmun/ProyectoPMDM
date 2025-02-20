package com.javidev.proyectopmdm.data.model

import com.squareup.moshi.Json

/**
 * Modelo de datos para recibir la respuesta de la API de Jikan.
 * Moshi se encarga de convertir el JSON en estos objetos de Kotlin.
 */
data class JikanResponse(
    val pagination: Pagination, // Información sobre la paginación
    val data: List<Anime> // Lista de animes obtenidos de la API
)

// Datos sobre la paginación de la API (última página visible, si hay más páginas disponibles)
data class Pagination(
    @Json(name = "last_visible_page") val lastVisiblePage: Int,
    @Json(name = "has_next_page") val hasNextPage: Boolean
)

// Representa un anime obtenido de la API
data class Anime(
    val mal_id: Int,
    val title: String,
    @Json(name = "title_english") val titleEnglish: String?,
    @Json(name = "images") val images: AnimeImages,
    val type: String?,
    val status: String?,
    val aired: Aired?,
    val genres: List<Genre>?,
    val studios: List<Studio>?,
    val episodes: Int?,
    val score: Double?,
    val synopsis: String?
)

// Contiene las URLs de las imágenes del anime en diferentes formatos
data class AnimeImages(
    @Json(name = "jpg") val jpg: ImageUrl,
    @Json(name = "webp") val webp: ImageUrl
)

// Contiene las URLs de las imágenes en distintos tamaños y calidades
data class ImageUrl(
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "large_image_url") val largeImageUrl: String?, // Imagen de mayor resolución
    @Json(name = "maximum_image_url") val maxImageUrl: String? // Imagen con máxima calidad
)

// Contiene la información sobre las fechas de emisión del anime
data class Aired(
    @Json(name = "from") val from: String?,
    @Json(name = "to") val to: String?
)

// Representa un género del anime (ejemplo: "Acción", "Aventura")
data class Genre(
    val name: String
)

// Representa un estudio de animación
data class Studio(
    val name: String
)
