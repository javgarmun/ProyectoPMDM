package com.javidev.proyectopmdm.data.api

import com.javidev.proyectopmdm.data.model.JikanResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para realizar peticiones a la API de Jikan utilizando Retrofit.
 */
interface JikanApiService {

    /**
     * Obtiene la lista de los animes mejor valorados.
     */
    @GET("top/anime")
    suspend fun getTopAnimes(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 24,
        @Query("order_by") orderBy: String = "score",
        @Query("sort") sort: String = "desc"
    ): JikanResponse

    /**
     * Busca animes por nombre en la API.
     */
    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25,
        @Query("sfw") sfw: Boolean = true
    ): JikanResponse
}
