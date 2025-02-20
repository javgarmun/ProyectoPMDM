package com.javidev.proyectopmdm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.javidev.proyectopmdm.data.api.RetrofitInstance
import com.javidev.proyectopmdm.data.local.AnimeDatabase
import com.javidev.proyectopmdm.data.local.AnimeEntity
import com.javidev.proyectopmdm.data.model.Anime
import com.javidev.proyectopmdm.data.repository.AnimeRepository
import kotlinx.coroutines.launch

class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    val animeList = MutableLiveData<List<Anime>>()
    private var isSearching = false
    var currentPage = 1
    private var lastPage = 1146 // Límite según la API

    private val repository: AnimeRepository

    init {
        val database = AnimeDatabase.getDatabase(application)
        repository = AnimeRepository(database.animeDao(), RetrofitInstance.api)
    }

    // Obtiene los datos de los animes guardados en caché
    val savedAnimes: LiveData<List<AnimeEntity>> = repository.getFavorites().asLiveData()

    fun fetchTopAnimes(page: Int = 1) {
        if (isSearching) return

        viewModelScope.launch {
            try {
                val animes = repository.getAnimesFromApi(page)
                animeList.postValue(animes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun nextPage() {
        if (!isSearching && currentPage < lastPage) {
            currentPage++
            fetchTopAnimes(currentPage)
        }
    }

    fun previousPage() {
        if (!isSearching && currentPage > 1) {
            currentPage--
            fetchTopAnimes(currentPage)
        }
    }

    fun searchAnime(query: String) {
        viewModelScope.launch {
            try {
                isSearching = true
                val response = RetrofitInstance.api.searchAnime(query)

                animeList.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSearching = false
            }
        }
    }

    fun resetAnimeList() {
        isSearching = false
        fetchTopAnimes(1)
    }

    fun saveAnime(anime: AnimeEntity) {
        viewModelScope.launch {
            repository.saveAnime(anime)
        }
    }

    fun deleteAnime(anime: AnimeEntity) {
        viewModelScope.launch {
            repository.deleteAnime(anime)
        }
    }
}
