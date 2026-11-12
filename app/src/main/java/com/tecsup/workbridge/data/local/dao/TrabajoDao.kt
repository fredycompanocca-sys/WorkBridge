package com.tecsup.workbridge.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tecsup.workbridge.data.local.entity.TrabajoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones CRUD en la base de datos local SQLite mediante Room.
 */
@Dao
interface TrabajoDao {

    @Query("SELECT * FROM trabajos ORDER BY fechaPublicacion DESC")
    fun getAllTrabajos(): Flow<List<TrabajoEntity>>

    @Query("SELECT * FROM trabajos WHERE esFavorito = 1 ORDER BY fechaPublicacion DESC")
    fun getFavoritos(): Flow<List<TrabajoEntity>>

    @Query("SELECT * FROM trabajos WHERE id = :id LIMIT 1")
    suspend fun getTrabajoById(id: String): TrabajoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrabajo(trabajo: TrabajoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrabajos(trabajos: List<TrabajoEntity>)

    @Query("UPDATE trabajos SET esFavorito = :esFavorito WHERE id = :id")
    suspend fun updateFavorito(id: String, esFavorito: Boolean)

    @Delete
    suspend fun deleteTrabajo(trabajo: TrabajoEntity)

    @Query("DELETE FROM trabajos WHERE id = :id")
    suspend fun deleteTrabajoById(id: String)

    @Query("DELETE FROM trabajos")
    suspend fun clearAll()
}
