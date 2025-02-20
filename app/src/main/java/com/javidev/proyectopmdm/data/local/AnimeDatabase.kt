package com.javidev.proyectopmdm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos de Room que gestiona la tabla de animes guardados como favoritos.
 */
@Database(entities = [AnimeEntity::class], version = 2, exportSchema = false)
abstract class AnimeDatabase : RoomDatabase() {

    // DAO para acceder a las operaciones de la base de datos
    abstract fun animeDao(): AnimeDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDatabase? = null

        /**
         * Obtiene la instancia de la base de datos.
         * Implementado como un Singleton para evitar múltiples instancias en la app.
         */
        fun getDatabase(context: Context): AnimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "anime_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
