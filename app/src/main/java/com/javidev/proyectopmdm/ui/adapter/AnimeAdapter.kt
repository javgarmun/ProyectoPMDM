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

/**
 * Adaptador para manejar la lista de animes en un RecyclerView.
 * Soporta tanto animes obtenidos de la API como animes guardados en favoritos (Room).
 */
class AnimeAdapter(
    private var animeList: MutableList<Anime> = mutableListOf(), // Lista de animes de la API
    private val isFavoriteList: Boolean = false, // Indica si el adaptador está mostrando la lista de favoritos
    private val onDeleteClick: ((AnimeEntity) -> Unit)? = null // Callback para eliminar un anime de favoritos
) : ListAdapter<AnimeEntity, AnimeAdapter.AnimeViewHolder>(DiffCallback()) {

    /**
     * ViewHolder que representa un elemento en la lista.
     */
    class AnimeViewHolder(private val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Asigna los datos de un anime al diseño del ítem en el RecyclerView.
         * Puede recibir un `Anime` (de la API) o un `AnimeEntity` (favorito en Room).
         */
        fun bind(anime: Any, isFavoriteList: Boolean, onDeleteClick: ((AnimeEntity) -> Unit)?) {
            val context = binding.root.context
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            // Variables para almacenar los datos del anime
            val title: String
            val imageUrl: String?
            val animeId: Int
            var animeJson: String? = null

            when (anime) {
                is Anime -> { // Si el anime proviene de la API
                    title = anime.title
                    imageUrl = anime.images.jpg.imageUrl
                    animeId = anime.mal_id
                    animeJson = moshi.adapter(Anime::class.java)
                        .toJson(anime) // Serializamos el anime completo para pasarlo a la siguiente actividad
                }

                is AnimeEntity -> { // Si el anime proviene de favoritos (Room)
                    title = anime.title
                    imageUrl = anime.imageUrl
                    animeId = anime.malId
                }

                else -> return // No hace nada si el tipo de dato es incorrecto
            }

            // Asignamos los datos al diseño del ítem
            binding.animeTitle.text = title
            Glide.with(context)
                .load(imageUrl)
                .fitCenter()
                .into(binding.animeImage)

            // Click para abrir detalles del anime
            binding.root.setOnClickListener {
                val intent = Intent(context, AnimeDetailActivity::class.java).apply {
                    putExtra("anime_id", animeId) // Pasamos el ID del anime
                    animeJson?.let {
                        putExtra(
                            "anime_json",
                            it
                        )
                    } // Si viene de la API, pasamos el JSON completo
                }
                context.startActivity(intent)
            }
        }
    }

    /**
     * Crea un nuevo ViewHolder inflando el layout correspondiente.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    /**
     * Asigna los datos al ViewHolder en la posición indicada.
     */
    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        if (isFavoriteList) {
            holder.bind(
                getItem(position),
                true,
                onDeleteClick
            ) // Si estamos en favoritos, usamos `AnimeEntity`
        } else {
            holder.bind(animeList[position], false, null) // Si es de la API, usamos `Anime`
        }
    }

    /**
     * Devuelve el número total de elementos en la lista.
     */
    override fun getItemCount(): Int = if (isFavoriteList) currentList.size else animeList.size

    /**
     * Actualiza la lista de animes obtenidos de la API.
     */
    fun updateList(newList: List<Anime>) {
        animeList.clear()
        animeList.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * Implementa `DiffUtil` para optimizar la actualización del RecyclerView.
     * Permite detectar cambios en la lista y actualizar solo los elementos necesarios.
     */
    class DiffCallback : DiffUtil.ItemCallback<AnimeEntity>() {
        override fun areItemsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean =
            oldItem.malId == newItem.malId

        override fun areContentsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean =
            oldItem == newItem
    }
}
