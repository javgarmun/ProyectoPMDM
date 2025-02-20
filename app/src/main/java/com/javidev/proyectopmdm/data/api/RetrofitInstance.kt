package com.javidev.proyectopmdm.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Configuración de Retrofit para realizar peticiones a la API de Jikan.
 */
object RetrofitInstance {
    private const val BASE_URL = "https://api.jikan.moe/v4/"

    // Configuración de Moshi para convertir JSON a objetos de Kotlin
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Inicialización de Retrofit con la URL base y el convertidor Moshi
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Instancia de la API para realizar peticiones desde cualquier parte de la app
    val api: JikanApiService by lazy {
        retrofit.create(JikanApiService::class.java)
    }
}
