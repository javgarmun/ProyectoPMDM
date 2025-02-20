package com.javidev.proyectopmdm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.javidev.proyectopmdm.data.api.RetrofitInstance
import com.javidev.proyectopmdm.data.local.AnimeDatabase
import com.javidev.proyectopmdm.data.local.AnimeEntity
import com.javidev.proyectopmdm.data.model.Anime
import com.javidev.proyectopmdm.data.repository.AnimeRepository
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja la lógica de la aplicación y la comunicación entre la UI y los datos.
 */
class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    val animeList = MutableLiveData<List<Anime>>() // Lista de animes obtenidos de la API

    var currentPage = 1 // Página actual en la paginación de la API
    private var lastPage = 1

    private var isSearching = false // Controla si se está realizando una búsqueda
    private var currentSearchQuery: String? = null // Guarda el término de búsqueda actual


    private val repository: AnimeRepository

    init {
        val database = AnimeDatabase.getDatabase(application)
        repository = AnimeRepository(database.animeDao(), RetrofitInstance.api)
    }

    // Obtiene los animes guardados como favoritos en la base de datos local
    val savedAnimes: LiveData<List<AnimeEntity>> = repository.getFavorites().asLiveData()

    /**
     * Obtiene la lista de animes desde la API y la almacena en `animeList`.
     */
    fun fetchTopAnimes(page: Int = 1) {
        if (isSearching) return

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTopAnimes(page) // Llamada a la API

                // Guardar la cantidad real de páginas desde la API
                lastPage = response.pagination.lastVisiblePage

                animeList.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Avanza a la siguiente página en la paginación de la API.
     */
    fun nextPage() {
        if (!isSearching && currentPage < lastPage) {
            currentPage++
            if (currentSearchQuery != null) {
                searchAnime(currentSearchQuery!!, currentPage) // Continúa la búsqueda
            } else {
                fetchTopAnimes(currentPage) // Si no hay búsqueda, carga animes normales
            }
        }
    }

    /**
     * Retrocede a la página anterior en la paginación de la API.
     */
    fun previousPage() {
        if (!isSearching && currentPage > 1) {
            currentPage--
            if (currentSearchQuery != null) {
                searchAnime(currentSearchQuery!!, currentPage) // Continúa la búsqueda
            } else {
                fetchTopAnimes(currentPage) // Si no hay búsqueda, carga animes normales
            }
        }
    }

    /**
     * Busca un anime por nombre en la API y actualiza `animeList` con los resultados.
     */
    fun searchAnime(query: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                isSearching = true
                currentSearchQuery = query // Guarda el término de búsqueda actual
                val response = RetrofitInstance.api.searchAnime(query, page)
                animeList.postValue(response.data)
                lastPage =
                    response.pagination.lastVisiblePage // Actualiza el número real de páginas
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSearching = false
            }
        }
    }


    /**
     * Reinicia la lista de animes mostrando los animes mejor valorados desde la API.
     */
    fun resetAnimeList() {
        isSearching = false
        fetchTopAnimes(1)
    }

    /**
     * Guarda un anime en la lista de favoritos.
     */
    fun saveAnime(anime: AnimeEntity) {
        viewModelScope.launch {
            repository.saveAnime(anime)
        }
    }

    /**
     * Elimina un anime de la lista de favoritos.
     */
    fun deleteAnime(anime: AnimeEntity) {
        viewModelScope.launch {
            repository.deleteAnime(anime)
        }
    }
}
