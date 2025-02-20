package com.javidev.proyectopmdm.ui

import android.annotation.SuppressLint
import android.os.Bundle
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

/**
 * `AnimeDetailActivity` muestra los detalles de un anime, ya sea de la API o guardado en favoritos.
 * Permite visualizar información del anime y guardarlo/eliminarlo de la lista de favoritos.
 */
class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeDetailBinding
    private val animeViewModel: AnimeViewModel by viewModels() // ViewModel para gestionar favoritos
    private var anime: Anime? = null // Para los animes cargados desde la API
    private var animeEntity: AnimeEntity? = null // Para los animes guardados en favoritos
    private var isFavorite = false // Indica si el anime está en favoritos

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animeId = intent.getIntExtra("anime_id", -1) // Obtener el ID del anime desde el intent
        val animeJson =
            intent.getStringExtra("anime_json") // Obtener el JSON del anime (si viene de la API)

        if (animeJson != null) {
            // Si el intent trae un JSON, significa que el anime viene de la API
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(Anime::class.java)
            anime = jsonAdapter.fromJson(animeJson)
            anime?.let { mostrarDetallesDesdeAnime(it) }

        } else if (animeId != -1) {
            // Si el intent solo trae un ID, buscamos el anime en la lista de favoritos (Room)
            animeViewModel.savedAnimes.observe(this, Observer { favoritos ->
                animeEntity = favoritos.find { it.malId == animeId }
                isFavorite = animeEntity != null

                if (animeEntity != null) {
                    // Si el anime está en favoritos, mostramos sus detalles
                    mostrarDetallesDesdeEntity(animeEntity!!)
                    actualizarBotonFavorito()
                } else {
                    // Si no se encuentra en favoritos, cerramos la actividad
                    finish()
                }
            })
        } else {
            // Si no hay JSON ni ID, mostramos un error y cerramos la actividad
            Toast.makeText(this, "Error al cargar el anime", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Botón para guardar/eliminar el anime de favoritos
        binding.saveAnimeButton.setOnClickListener {
            if (isFavorite && animeEntity != null) {
                animeViewModel.deleteAnime(animeEntity!!) // Eliminar de favoritos
                Toast.makeText(this, "Anime eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else if (anime != null) {
                val animeEntity =
                    convertirAEntidad(anime!!) // Convertirlo en `AnimeEntity` antes de guardarlo
                animeViewModel.saveAnime(animeEntity)
                Toast.makeText(this, "Anime guardado en favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para volver a la pantalla anterior
        binding.backButton.setOnClickListener { finish() }
    }

    /**
     * Muestra los detalles de un anime obtenido desde la API.
     */
    @SuppressLint("SetTextI18n")
    private fun mostrarDetallesDesdeAnime(anime: Anime) {
        binding.animeTitle.text = anime.title
        binding.animeEnglishTitle.text =
            anime.titleEnglish?.takeIf { it.isNotEmpty() } ?: anime.title
        binding.animeType.text = "Tipo: ${anime.type ?: "Desconocido"}"
        binding.animeScore.text = "Puntuación: ${anime.score ?: "N/A"}"
        binding.animeEpisodes.text = "Episodios: ${anime.episodes ?: "Desconocido"}"
        binding.animeSynopsis.text = anime.synopsis ?: "Sin sinopsis disponible"
        binding.animeStatus.text = "Estado: ${anime.status ?: "Desconocido"}"
        binding.animeGenres.text =
            "Géneros: ${anime.genres?.joinToString(", ") { it.name } ?: "No disponible"}"
        binding.animeStudios.text =
            "Estudio: ${anime.studios?.joinToString(", ") { it.name } ?: "No disponible"}"

        // Cargar la imagen con Glide
        Glide.with(this)
            .load(
                anime.images.webp.maxImageUrl ?: anime.images.webp.largeImageUrl
                ?: anime.images.jpg.imageUrl
            )
            .override(800, 1000)
            .fitCenter()
            .into(binding.animeImage)
    }

    /**
     * Muestra los detalles de un anime guardado en la base de datos local (Room).
     */
    @SuppressLint("SetTextI18n")
    private fun mostrarDetallesDesdeEntity(anime: AnimeEntity) {
        binding.animeTitle.text = anime.title
        binding.animeEnglishTitle.text =
            anime.titleEnglish?.takeIf { it.isNotEmpty() } ?: anime.title
        binding.animeType.text = "Tipo: ${anime.type}"
        binding.animeScore.text = "Puntuación: ${anime.score}"
        binding.animeEpisodes.text = "Episodios: ${anime.episodes}"
        binding.animeSynopsis.text = anime.synopsis
        binding.animeStatus.text = "Estado: ${anime.status}"
        binding.animeGenres.text = "Géneros: ${anime.genres}"
        binding.animeStudios.text = "Estudio: ${anime.studios}"

        // Cargar la imagen con Glide
        Glide.with(this)
            .load(anime.imageUrl)
            .override(800, 1000)
            .fitCenter()
            .into(binding.animeImage)
    }

    /**
     * Actualiza el texto del botón de favoritos según si el anime está guardado o no.
     */
    private fun actualizarBotonFavorito() {
        binding.saveAnimeButton.text =
            if (isFavorite) "Eliminar de favoritos" else "Guardar en favoritos"
    }

    /**
     * Convierte un objeto `Anime` (de la API) en `AnimeEntity` para guardarlo en la base de datos.
     */
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
