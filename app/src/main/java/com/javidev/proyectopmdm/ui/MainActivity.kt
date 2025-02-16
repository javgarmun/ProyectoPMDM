package com.javidev.proyectopmdm.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            animeAdapter.updateList(animes)
        }

        // Cargar la lista infinita de animes por defecto
        animeViewModel.fetchAnimeList()

        // Detectar búsqueda
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                animeViewModel.searchAnime(query)
            }
        }

        // Detectar si el usuario borra la búsqueda
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    animeViewModel.resetAnimeList()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Detectar scroll para cargar más animes
        binding.recyclerViewAnime.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!animeViewModel.isCurrentlySearching() && visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                    animeViewModel.fetchAnimeList()
                }
            }
        })
    }
}
