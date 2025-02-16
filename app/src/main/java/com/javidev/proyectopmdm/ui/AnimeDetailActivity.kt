package com.javidev.proyectopmdm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.javidev.proyectopmdm.databinding.ActivityAnimeDetailBinding
import com.javidev.proyectopmdm.data.model.Anime
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AnimeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de Moshi para deserializar
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi.adapter(Anime::class.java)

        // Recibir el JSON desde el Intent
        val animeJson = intent.getStringExtra("anime_json")

        // Convertir el JSON de vuelta a un objeto Anime
        val anime = animeJson?.let { jsonAdapter.fromJson(it) }

        anime?.let {
            binding.animeTitle.text = it.title
            binding.animeScore.text = "Puntuación: ${it.score ?: "N/A"}"
            binding.animeEpisodes.text = "Episodios: ${it.episodes ?: "Desconocido"}"
            binding.animeSynopsis.text = it.synopsis ?: "Sin sinopsis disponible"

            Glide.with(this)
                .load(it.images.jpg.imageUrl)
                .fitCenter()
                .into(binding.animeImage)
        }

        // Botón de volver atrás
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
