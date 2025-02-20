package com.javidev.proyectopmdm.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) que define las operaciones con la base de datos de Room.
 * Se encarga de manejar los favoritos guardados por el usuario.
 */
@Dao
interface AnimeDao {

    // Insertar un anime en la base de datos (si ya existe, lo reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    // Obtener todos los animes guardados en la base de datos (favoritos)
    @Query("SELECT * FROM anime_table")
    fun getAllAnimes(): Flow<List<AnimeEntity>>

    // Obtener un anime específico por su ID
    @Query("SELECT * FROM anime_table WHERE malId = :id")
    suspend fun getAnimeById(id: Int): AnimeEntity?

    // Eliminar un anime de los favoritos
    @Delete
    suspend fun deleteAnime(anime: AnimeEntity)

    // Eliminar todos los animes guardados (limpiar la tabla)
    @Query("DELETE FROM anime_table")
    suspend fun clearAll()
}
