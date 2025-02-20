package com.javidev.proyectopmdm.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javidev.proyectopmdm.data.local.AnimeEntity
import com.javidev.proyectopmdm.data.model.Anime
import com.javidev.proyectopmdm.databinding.ItemAnimeBinding
import com.javidev.proyectopmdm.ui.AnimeDetailActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AnimeAdapter(
    private var animeList: MutableList<Anime> = mutableListOf(), // Solo usado en `MainActivity`
    private val isFavoriteList: Boolean = false, // Indica si estamos en la pantalla de favoritos
    private val onDeleteClick: ((AnimeEntity) -> Unit)? = null // Para eliminar de favoritos
) : ListAdapter<AnimeEntity, AnimeAdapter.AnimeViewHolder>(DiffCallback()) {

    class AnimeViewHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Any, isFavoriteList: Boolean, onDeleteClick: ((AnimeEntity) -> Unit)?) {
            when (anime) {
                is Anime -> { // Si es `Anime` (de la API)
                    binding.animeTitle.text = anime.title

                    Glide.with(binding.root.context)
                        .load(anime.images.jpg.imageUrl)
                        .fitCenter()
                        .into(binding.animeImage)

                    // Click para ver detalles
                    binding.root.setOnClickListener {
                        val context = binding.root.context
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val jsonAdapter = moshi.adapter(Anime::class.java)
                        val animeJson = jsonAdapter.toJson(anime)

                        val intent = Intent(context, AnimeDetailActivity::class.java).apply {
                            putExtra("anime_json", animeJson)
                        }
                        context.startActivity(intent)
                    }
                }

                is AnimeEntity -> { // Si es `AnimeEntity` (favoritos en Room)
                    binding.animeTitle.text = anime.title

                    Glide.with(binding.root.context)
                        .load(anime.imageUrl)
                        .fitCenter()
                        .into(binding.animeImage)

                    // Click para ver detalles de favoritos
                    binding.root.setOnClickListener {
                        val context = binding.root.context
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val jsonAdapter = moshi.adapter(AnimeEntity::class.java)
                        val animeJson = jsonAdapter.toJson(anime)

                        val intent = Intent(context, AnimeDetailActivity::class.java).apply {
                            putExtra("anime_json", animeJson)
                        }
                        context.startActivity(intent)
                    }

                    // Long Click para eliminar de favoritos
                    if (isFavoriteList) {
                        binding.root.setOnLongClickListener {
                            onDeleteClick?.invoke(anime)
                            true
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        if (isFavoriteList) {
            holder.bind(
                getItem(position),
                true,
                onDeleteClick
            )
        } else {
            holder.bind(animeList[position], false, null)
        }
    }

    override fun getItemCount(): Int = if (isFavoriteList) currentList.size else animeList.size

    fun updateList(newList: List<Anime>) {
        animeList.clear()
        animeList.addAll(newList)
        notifyDataSetChanged()
    }

    class DiffCallback : DiffUtil.ItemCallback<AnimeEntity>() {
        override fun areItemsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean =
            oldItem.malId == newItem.malId

        override fun areContentsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean =
            oldItem == newItem
    }
}
