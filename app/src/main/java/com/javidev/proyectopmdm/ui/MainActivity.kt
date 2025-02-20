package com.javidev.proyectopmdm.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.javidev.proyectopmdm.databinding.ActivityMainBinding
import com.javidev.proyectopmdm.ui.adapter.AnimeAdapter
import com.javidev.proyectopmdm.ui.viewmodel.AnimeViewModel

class MainActivity : AppCompatActivity() {

    private val animeViewModel: AnimeViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animeAdapter = AnimeAdapter(mutableListOf())
        binding.recyclerViewAnime.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewAnime.adapter = animeAdapter

        animeViewModel.animeList.observe(this) { animes ->
            Log.d("AnimeDebug", "Lista de animes cargados desde API: $animes")
            animeAdapter.updateList(animes ?: emptyList())
            binding.pageNumberText.text = "Página ${animeViewModel.currentPage}"

            binding.recyclerViewAnime.post {
                binding.recyclerViewAnime.adapter = animeAdapter
            }
        }

        // Cargar la primera página de Top Animes
        animeViewModel.fetchTopAnimes()

        // Detectar búsqueda
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                animeViewModel.searchAnime(query)
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    animeViewModel.resetAnimeList()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Botones de navegación entre páginas
        binding.prevPageButton.setOnClickListener { animeViewModel.previousPage() }
        binding.nextPageButton.setOnClickListener { animeViewModel.nextPage() }

        // Botón para ver favoritos
        binding.buttonFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }
}
