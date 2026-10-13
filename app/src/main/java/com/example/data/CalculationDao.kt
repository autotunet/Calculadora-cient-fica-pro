package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT 100")
    fun getAllHistory(): Flow<List<CalculationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(item: CalculationItem): Long

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
