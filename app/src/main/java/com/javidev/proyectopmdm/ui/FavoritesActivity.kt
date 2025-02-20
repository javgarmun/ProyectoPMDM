package com.javidev.proyectopmdm.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.javidev.proyectopmdm.databinding.ActivityFavoritesBinding
import com.javidev.proyectopmdm.ui.adapter.AnimeAdapter
import com.javidev.proyectopmdm.ui.viewmodel.AnimeViewModel

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val animeViewModel: AnimeViewModel by viewModels()
    private lateinit var adapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        adapter = AnimeAdapter(isFavoriteList = true)
        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewFavorites.adapter = adapter

        // Observar los datos guardados en Room
        animeViewModel.savedAnimes.observe(this) { animes ->
            Log.d("AnimeDebug", "Lista de favoritos desde Room: $animes")
            adapter.submitList(animes) // Actualiza la RecyclerView
        }
    }
}
