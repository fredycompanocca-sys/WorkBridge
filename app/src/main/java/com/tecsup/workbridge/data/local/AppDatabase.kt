package com.tecsup.workbridge.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.workbridge.data.local.dao.TrabajoDao
import com.tecsup.workbridge.data.local.entity.TrabajoEntity

/**
 * Base de datos principal de Room (SQLite) para la aplicación WorkBridge.
 * Implementa el patrón Singleton para asegurar una única instancia de conexión.
 */
@Database(
    entities = [TrabajoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trabajoDao(): TrabajoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workbridge_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
