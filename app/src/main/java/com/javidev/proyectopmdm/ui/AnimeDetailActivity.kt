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

/**
 * Pantalla de detalles de un anime.
 * Permite visualizar información detallada y guardar/eliminar el anime de favoritos.
 */
class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeDetailBinding
    private val animeViewModel: AnimeViewModel by viewModels() // ViewModel para manejar favoritos
    private var anime: Any? = null // Variable que almacena el anime actual (de la API o Room)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el JSON del anime desde el Intent
        val animeJson = intent.getStringExtra("anime_json")
        Log.d("AnimeDebug", "JSON recibido: $animeJson")

        if (animeJson != null) {
            // Convertir el JSON a un objeto Anime o AnimeEntity con Moshi
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapterAnime = moshi.adapter(Anime::class.java)
            val jsonAdapterEntity = moshi.adapter(AnimeEntity::class.java)

            anime = try {
                jsonAdapterAnime.fromJson(animeJson) // Intentar convertir a `Anime`
            } catch (e: Exception) {
                jsonAdapterEntity.fromJson(animeJson) // Si falla, intentar con `AnimeEntity`
            }

            Log.d("AnimeDebug", "Objeto deserializado: $anime")

            // Mostrar los detalles del anime en la pantalla
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

        // Configurar el estado del botón de favoritos según si el anime ya está guardado
        if (anime is AnimeEntity) {
            binding.saveAnimeButton.text = "Eliminar de favoritos"
        } else {
            binding.saveAnimeButton.text = "Guardar en favoritos"
        }

        // Guardar o eliminar de favoritos y deshabilitar el botón tras la acción
        binding.saveAnimeButton.setOnClickListener {
            when (anime) {
                is Anime -> { // Guardar en favoritos
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
                    animeViewModel.saveAnime(animeEntity)
                    Toast.makeText(this, "Anime guardado en favoritos", Toast.LENGTH_SHORT).show()

                    binding.saveAnimeButton.isEnabled = false // Deshabilitar el botón
                }

                is AnimeEntity -> { // Eliminar de favoritos
                    val animeEntity = anime as AnimeEntity
                    Log.d("AnimeDebug", "Eliminando de favoritos: $animeEntity")
                    animeViewModel.deleteAnime(animeEntity)
                    Toast.makeText(this, "Anime eliminado de favoritos", Toast.LENGTH_SHORT).show()

                    binding.saveAnimeButton.isEnabled = false // Deshabilitar el botón
                }
            }
        }

        // Botón para regresar a la pantalla anterior
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Muestra los detalles del anime en la interfaz.
     */
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

    /**
     * Convierte una fecha de formato "yyyy-MM-dd'T'HH:mm:ssXXX" a "dd/MM/yyyy".
     */
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Desconocido"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "Desconocido"
        } catch (e: Exception) {
            "Desconocido"
        }
    }
}
