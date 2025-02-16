package com.javidev.proyectopmdm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javidev.proyectopmdm.data.model.Anime
import com.javidev.proyectopmdm.databinding.ItemAnimeBinding

class AnimeAdapter(private val animeList: MutableList<Anime>) :
    RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime) {
            binding.animeTitle.text = anime.title
            binding.animeScore.text = "Puntuación: ${anime.score ?: "N/A"}"

            Glide.with(binding.animeImage.context)
                .load(anime.images.jpg.imageUrl)
                .fitCenter()
                .into(binding.animeImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(animeList[position])
    }

    override fun getItemCount(): Int = animeList.size

    // ✅ Agregamos la función para actualizar la lista de animes
    fun updateList(newAnimes: List<Anime>) {
        animeList.clear() // Limpiamos la lista anterior
        animeList.addAll(newAnimes) // Agregamos los nuevos animes
        notifyDataSetChanged() // Notificamos al RecyclerView para actualizar la UI
    }
}
