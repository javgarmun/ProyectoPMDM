package com.javidev.proyectopmdm.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.javidev.proyectopmdm.data.local.AnimeEntity
import com.javidev.proyectopmdm.databinding.ActivityAnimeDetailBinding
import com.javidev.proyectopmdm.data.model.Anime
import com.javidev.proyectopmdm.ui.viewmodel.AnimeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeDetailBinding
    private val animeViewModel: AnimeViewModel by viewModels()
    private var anime: Anime? = null
    private var animeEntity: AnimeEntity? = null
    private var isFavorite = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animeId = intent.getIntExtra("anime_id", -1)
        val animeJson = intent.getStringExtra("anime_json")

        if (animeJson != null) {
            // Si tenemos JSON, viene desde la API → lo cargamos directamente
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(Anime::class.java)
            anime = jsonAdapter.fromJson(animeJson)
            anime?.let { mostrarDetallesDesdeAnime(it) }

        } else if (animeId != -1) {
            // Si NO hay JSON pero sí `anime_id`, buscamos en favoritos
            animeViewModel.savedAnimes.observe(this, Observer { favoritos ->
                animeEntity = favoritos.find { it.malId == animeId }
                isFavorite = animeEntity != null

                if (animeEntity != null) {
                    mostrarDetallesDesdeEntity(animeEntity!!)
                    actualizarBotonFavorito()
                } else {
                    finish()
                }
            })
        } else {
            Toast.makeText(this, "Error al cargar el anime", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Botón de favoritos
        binding.saveAnimeButton.setOnClickListener {
            if (isFavorite && animeEntity != null) {
                animeViewModel.deleteAnime(animeEntity!!)
                Toast.makeText(this, "Anime eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else if (anime != null) {
                val animeEntity = convertirAEntidad(anime!!)
                animeViewModel.saveAnime(animeEntity)
                Toast.makeText(this, "Anime guardado en favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener { finish() }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDetallesDesdeAnime(anime: Anime) {
        binding.animeTitle.text = anime.title
        binding.animeEnglishTitle.text = anime.titleEnglish?.takeIf { it.isNotEmpty() } ?: anime.title
        binding.animeType.text = "Tipo: ${anime.type ?: "Desconocido"}"
        binding.animeScore.text = "Puntuación: ${anime.score ?: "N/A"}"
        binding.animeEpisodes.text = "Episodios: ${anime.episodes ?: "Desconocido"}"
        binding.animeSynopsis.text = anime.synopsis ?: "Sin sinopsis disponible"
        binding.animeStatus.text = "Estado: ${anime.status ?: "Desconocido"}"
        binding.animeGenres.text = "Géneros: ${anime.genres?.joinToString(", ") { it.name } ?: "No disponible"}"
        binding.animeStudios.text = "Estudio: ${anime.studios?.joinToString(", ") { it.name } ?: "No disponible"}"

        Glide.with(this)
            .load(anime.images.webp.maxImageUrl ?: anime.images.webp.largeImageUrl ?: anime.images.jpg.imageUrl)
            .override(800, 1000)
            .fitCenter()
            .into(binding.animeImage)
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDetallesDesdeEntity(anime: AnimeEntity) {
        binding.animeTitle.text = anime.title
        binding.animeEnglishTitle.text = anime.titleEnglish?.takeIf { it.isNotEmpty() } ?: anime.title
        binding.animeType.text = "Tipo: ${anime.type}"
        binding.animeScore.text = "Puntuación: ${anime.score}"
        binding.animeEpisodes.text = "Episodios: ${anime.episodes}"
        binding.animeSynopsis.text = anime.synopsis
        binding.animeStatus.text = "Estado: ${anime.status}"
        binding.animeGenres.text = "Géneros: ${anime.genres}"
        binding.animeStudios.text = "Estudio: ${anime.studios}"

        Glide.with(this)
            .load(anime.imageUrl)
            .override(800, 1000)
            .fitCenter()
            .into(binding.animeImage)
    }

    private fun actualizarBotonFavorito() {
        binding.saveAnimeButton.text = if (isFavorite) "Eliminar de favoritos" else "Guardar en favoritos"
    }

    private fun convertirAEntidad(anime: Anime): AnimeEntity {
        return AnimeEntity(
            malId = anime.mal_id,
            title = anime.title,
            titleEnglish = anime.titleEnglish ?: "",
            imageUrl = anime.images.jpg.imageUrl,
            type = anime.type ?: "Desconocido",
            episodes = anime.episodes ?: 0,
            score = anime.score ?: 0.0,
            synopsis = anime.synopsis ?: "No disponible",
            status = anime.status ?: "Desconocido",
            airedFrom = anime.aired?.from ?: "Desconocido",
            airedTo = anime.aired?.to ?: "Desconocido",
            genres = anime.genres?.joinToString(", ") { it.name } ?: "No disponible",
            studios = anime.studios?.joinToString(", ") { it.name } ?: "No disponible"
        )
    }
}
