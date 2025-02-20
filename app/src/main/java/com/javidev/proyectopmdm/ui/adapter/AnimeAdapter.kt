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
    private var animeList: MutableList<Anime> = mutableListOf(), // Lista de animes de la API
    private val isFavoriteList: Boolean = false, // Indica si es la lista de favoritos
    private val onDeleteClick: ((AnimeEntity) -> Unit)? = null // Callback para eliminar de favoritos
) : ListAdapter<AnimeEntity, AnimeAdapter.AnimeViewHolder>(DiffCallback()) {

    class AnimeViewHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Any, isFavoriteList: Boolean, onDeleteClick: ((AnimeEntity) -> Unit)?) {
            val context = binding.root.context
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val title: String
            val imageUrl: String?
            val animeId: Int
            var animeJson: String? = null

            when (anime) {
                is Anime -> { // Si es un anime de la API
                    title = anime.title
                    imageUrl = anime.images.jpg.imageUrl
                    animeId = anime.mal_id
                    animeJson = moshi.adapter(Anime::class.java)
                        .toJson(anime) // Serializamos el anime completo
                }

                is AnimeEntity -> { // Si es un anime guardado en favoritos
                    title = anime.title
                    imageUrl = anime.imageUrl
                    animeId = anime.malId
                }

                else -> return
            }

            binding.animeTitle.text = title
            Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(binding.animeImage)

            // Click para abrir detalles
            binding.root.setOnClickListener {
                val intent = Intent(context, AnimeDetailActivity::class.java).apply {
                    putExtra("anime_id", animeId)
                    animeJson?.let { putExtra("anime_json", it) } // Solo lo agregamos si existe
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        if (isFavoriteList) {
            holder.bind(getItem(position), true, onDeleteClick)
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
