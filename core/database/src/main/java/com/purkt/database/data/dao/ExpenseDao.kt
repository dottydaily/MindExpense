package com.purkt.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.purkt.database.data.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense")
    fun findAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expense WHERE id=:id")
    suspend fun findById(id: Int): ExpenseEntity?

    @Query("SELECT COUNT(*) FROM expense")
    suspend fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg expenses: ExpenseEntity): List<Long>

    @Update
    suspend fun update(vararg expenses: ExpenseEntity): Int

    @Delete
    suspend fun delete(vararg expense: ExpenseEntity): Int

    @Query("DELETE FROM expense")
    suspend fun deleteAll(): Int
}
