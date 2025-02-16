package com.javidev.proyectopmdm.data.api

import com.javidev.proyectopmdm.data.model.JikanResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApiService {

    // Obtener animes por página
    @GET("anime")
    suspend fun getAnimeList(@Query("page") page: Int): JikanResponse

    // Obtener animes populares
    @GET("top/anime")
    suspend fun getTopAnime(): JikanResponse

    // Buscar anime por nombre
    @GET("anime")
    suspend fun searchAnime(@Query("q") query: String): JikanResponse
}