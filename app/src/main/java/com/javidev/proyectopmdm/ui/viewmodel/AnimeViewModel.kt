package com.javidev.proyectopmdm.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javidev.proyectopmdm.data.api.RetrofitInstance
import com.javidev.proyectopmdm.data.model.Anime
import kotlinx.coroutines.launch

class AnimeViewModel : ViewModel() {
    val animeList = MutableLiveData<List<Anime>>()
    private var currentPage = 1
    private val loadedAnimes = mutableListOf<Anime>()
    private var isSearching = false

    fun fetchAnimeList() {
        if (isSearching) return // No cargar más si estamos en modo búsqueda

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAnimeList(currentPage)
                loadedAnimes.addAll(response.data)
                animeList.postValue(loadedAnimes)
                currentPage++ // Avanza a la siguiente página
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchAnime(query: String) {
        viewModelScope.launch {
            try {
                isSearching = true // Entramos en modo búsqueda
                val response = RetrofitInstance.api.searchAnime(query)
                animeList.postValue(response.data) // Reemplazamos la lista con los resultados
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetAnimeList() {
        isSearching = false
        loadedAnimes.clear()
        currentPage = 1
        fetchAnimeList()
    }

    fun isCurrentlySearching(): Boolean {
        return isSearching
    }
}