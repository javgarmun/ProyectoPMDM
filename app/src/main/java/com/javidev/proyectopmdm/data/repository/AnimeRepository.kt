package com.javidev.proyectopmdm.data.repository

import com.javidev.proyectopmdm.data.api.JikanApiService
import com.javidev.proyectopmdm.data.local.AnimeDao
import com.javidev.proyectopmdm.data.local.AnimeEntity
import com.javidev.proyectopmdm.data.model.Anime
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que actúa como intermediario entre la API y la base de datos local.
 * Gestiona las operaciones de obtener, guardar y eliminar animes.
 */
class AnimeRepository(private val animeDao: AnimeDao, private val apiService: JikanApiService) {

    /**
     * Obtiene una lista de animes desde la API.
     * En caso de error, devuelve una lista vacía.
     */
    suspend fun getAnimesFromApi(page: Int): List<Anime> {
        return try {
            val response = apiService.getTopAnimes(page)
            response.data
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Obtiene la lista de animes guardados en favoritos desde la base de datos local (Room).
     */
    fun getFavorites(): Flow<List<AnimeEntity>> {
        return animeDao.getAllAnimes()
    }

    /**
     * Guarda un anime en la lista de favoritos.
     */
    suspend fun saveAnime(animeEntity: AnimeEntity) {
        animeDao.insertAnime(animeEntity)
    }

    /**
     * Elimina un anime de la lista de favoritos.
     */
    suspend fun deleteAnime(animeEntity: AnimeEntity) {
        animeDao.deleteAnime(animeEntity)
    }
}
