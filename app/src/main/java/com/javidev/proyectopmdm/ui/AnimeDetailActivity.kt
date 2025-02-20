package com.javidev.proyectopmdm.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.javidev.proyectopmdm.data.local.AnimeEntity
import com.javidev.proyectopmdm.databinding.ActivityAnimeDetailBinding
import com.javidev.proyectopmdm.data.model.Anime
import com.javidev.proyectopmdm.ui.viewmodel.AnimeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeDetailBinding
    private val animeViewModel: AnimeViewModel by viewModels() // ✅ Sigue siendo necesario para guardar favoritos
    private var anime: Any? = null // ✅ Para almacenar el anime actual (de la API o Room)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el JSON desde el Intent
        val animeJson = intent.getStringExtra("anime_json")
        Log.d("AnimeDebug", "JSON recibido: $animeJson")

        if (animeJson != null) {
            // Configurar Moshi para convertir ambos tipos de objetos
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapterAnime = moshi.adapter(Anime::class.java)
            val jsonAdapterEntity = moshi.adapter(AnimeEntity::class.java)

            anime = try {
                jsonAdapterAnime.fromJson(animeJson) // ✅ Intenta convertir como `Anime`
            } catch (e: Exception) {
                jsonAdapterEntity.fromJson(animeJson) // ✅ Si falla, intenta como `AnimeEntity`
            }

            Log.d("AnimeDebug", "Objeto deserializado: $anime")

            anime?.let {
                when (it) {
                    is Anime -> mostrarDetalles(
                        it.title, it.titleEnglish, it.type, it.score, it.episodes,
                        it.synopsis, it.status, it.aired?.from, it.aired?.to,
                        it.genres?.joinToString(", ") { genre -> genre.name } ?: "No disponible",
                        it.studios?.joinToString(", ") { studio -> studio.name } ?: "No disponible",
                        it.images.webp.maxImageUrl ?: it.images.webp.largeImageUrl
                        ?: it.images.jpg.imageUrl
                    )

                    is AnimeEntity -> mostrarDetalles(
                        it.title, it.titleEnglish, it.type, it.score, it.episodes,
                        it.synopsis, "Desconocido", null, null, "No disponible", "No disponible",
                        it.imageUrl
                    )
                }
            }
        } else {
            Toast.makeText(this, "Error: No se pudo cargar el anime", Toast.LENGTH_SHORT).show()
        }

        // Configurar el estado inicial del botón
        if (anime is AnimeEntity) {
            binding.saveAnimeButton.text = "Eliminar de favoritos"
        } else {
            binding.saveAnimeButton.text = "Guardar en favoritos"
        }

        // Guardar o eliminar de favoritos y deshabilitar el botón
        binding.saveAnimeButton.setOnClickListener {
            when (anime) {
                is Anime -> { // ✅ Guardar en favoritos
                    val animeData = anime as Anime
                    val animeEntity = AnimeEntity(
                        malId = animeData.mal_id,
                        title = animeData.title,
                        titleEnglish = animeData.titleEnglish ?: "",
                        imageUrl = animeData.images.jpg.imageUrl,
                        type = animeData.type ?: "Desconocido",
                        episodes = animeData.episodes ?: 0,
                        score = animeData.score ?: 0.0,
                        synopsis = animeData.synopsis ?: "No disponible"
                    )

                    Log.d("AnimeDebug", "Guardando en favoritos: $animeEntity")
                    animeViewModel.saveAnime(animeEntity) // ✅ Guardar en Room
                    Toast.makeText(this, "Anime guardado en favoritos", Toast.LENGTH_SHORT).show()

                    binding.saveAnimeButton.isEnabled =
                        false // 🔥 Deshabilitar el botón tras guardar
                }

                is AnimeEntity -> { // ✅ Eliminar de favoritos
                    val animeEntity = anime as AnimeEntity
                    Log.d("AnimeDebug", "Eliminando de favoritos: $animeEntity")
                    animeViewModel.deleteAnime(animeEntity) // ✅ Eliminar de Room
                    Toast.makeText(this, "Anime eliminado de favoritos", Toast.LENGTH_SHORT).show()

                    binding.saveAnimeButton.isEnabled =
                        false // 🔥 Deshabilitar el botón tras eliminar
                }
            }
        }


        // Botón de volver atrás
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDetalles(
        title: String, titleEnglish: String?, type: String?, score: Double?, episodes: Int?,
        synopsis: String?, status: String?, airedFrom: String?, airedTo: String?,
        genres: String, studios: String, imageUrl: String?
    ) {
        binding.animeTitle.text = title
        binding.animeEnglishTitle.text = titleEnglish?.takeIf { it.isNotEmpty() } ?: title
        binding.animeType.text = "Tipo: ${type ?: "Desconocido"}"
        binding.animeScore.text = "Puntuación: ${score ?: "N/A"}"
        binding.animeEpisodes.text = "Episodios: ${episodes ?: "Desconocido"}"
        binding.animeSynopsis.text = synopsis ?: "Sin sinopsis disponible"
        binding.animeStatus.text = "Estado: ${status ?: "Desconocido"}"
        binding.animeReleaseDate.text = "Estreno: ${formatDate(airedFrom)}"

        val endDate = formatDate(airedTo)
        if (endDate == "Desconocido") {
            binding.animeEndDate.visibility = View.GONE
        } else {
            binding.animeEndDate.text = "Finalización: $endDate"
            binding.animeEndDate.visibility = View.VISIBLE
        }

        binding.animeGenres.text = "Géneros: $genres"
        binding.animeStudios.text = "Estudio: $studios"

        Glide.with(this)
            .load(imageUrl)
            .override(800, 1000)
            .fitCenter()
            .into(binding.animeImage)
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Desconocido"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val outputFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato DD/MM/AAAA
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "Desconocido"
        } catch (e: Exception) {
            "Desconocido"
        }
    }
}
