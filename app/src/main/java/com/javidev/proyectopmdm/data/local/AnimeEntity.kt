package com.javidev.proyectopmdm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa un anime guardado en la base de datos local (favoritos).
 */
@Entity(tableName = "anime_table")
data class AnimeEntity(
    @PrimaryKey val malId: Int, // ID único del anime
    val title: String, // Título en japonés o por defecto
    val titleEnglish: String?, // Título en inglés (puede ser nulo)
    val imageUrl: String, // URL de la imagen del anime
    val type: String?, // Tipo de anime (TV, OVA, Película, etc.)
    val episodes: Int?, // Número de episodios (puede ser desconocido)
    val score: Double?, // Puntuación del anime
    val synopsis: String?, // Sinopsis del anime
    val status: String?, // Estado del anime
    val airedFrom: String?, // Fecha de estreno
    val airedTo: String?, // Fecha de finalización
    val genres: String?, // Géneros del anime
    val studios: String? // Estudios de animación
)
