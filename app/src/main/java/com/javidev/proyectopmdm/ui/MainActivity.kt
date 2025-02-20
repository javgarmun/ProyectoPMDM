package com.javidev.proyectopmdm.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.javidev.proyectopmdm.databinding.ActivityMainBinding
import com.javidev.proyectopmdm.ui.adapter.AnimeAdapter
import com.javidev.proyectopmdm.ui.viewmodel.AnimeViewModel

/**
 * Pantalla principal de la aplicación.
 * Muestra la lista de animes obtenidos de la API y permite la búsqueda y paginación.
 */
class MainActivity : AppCompatActivity() {

    private val animeViewModel: AnimeViewModel by viewModels() // ViewModel para manejar la lógica de datos
    private lateinit var binding: ActivityMainBinding
    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del RecyclerView para mostrar los animes en una cuadrícula de 2 columnas
        animeAdapter = AnimeAdapter(mutableListOf())
        binding.recyclerViewAnime.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewAnime.adapter = animeAdapter

        // Observamos la lista de animes y actualizamos la UI
        animeViewModel.animeList.observe(this) { animes ->
            animeAdapter.updateList(animes ?: emptyList())
            binding.pageNumberText.text = "Página ${animeViewModel.currentPage}"

            // 🔹 Deshabilitar "Anterior" si estamos en la página 1
            binding.prevPageButton.isEnabled = animeViewModel.currentPage > 1

            // 🔹 Deshabilitar "Siguiente" si estamos en la última página
            binding.nextPageButton.isEnabled = animeViewModel.currentPage < animeViewModel.lastPage

            binding.recyclerViewAnime.post {
                binding.recyclerViewAnime.adapter = animeAdapter
            }
        }

        // Cargar la primera página de animes populares al iniciar la app
        animeViewModel.fetchTopAnimes()

        // Detectar búsqueda de animes
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                animeViewModel.searchAnime(query)
            }
        }

        // Restaurar la lista de animes si el campo de búsqueda queda vacío
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    animeViewModel.resetAnimeList() // Ahora sí restablece la lista correctamente
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // Botones para navegar entre páginas de animes
        binding.prevPageButton.setOnClickListener { animeViewModel.previousPage() }
        binding.nextPageButton.setOnClickListener { animeViewModel.nextPage() }

        // Botón para ver la lista de favoritos guardados en Room
        binding.buttonFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }
}
