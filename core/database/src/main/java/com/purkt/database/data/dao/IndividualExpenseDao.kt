package com.purkt.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.purkt.database.data.entity.IndividualExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IndividualExpenseDao {
    @Query("SELECT * FROM individual_expense")
    fun findAll(): Flow<List<IndividualExpenseEntity>>

    @Query("SELECT * FROM individual_expense WHERE id=:id")
    suspend fun findById(id: Int): IndividualExpenseEntity?

    @Query("SELECT COUNT(*) FROM individual_expense")
    suspend fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg expenses: IndividualExpenseEntity): List<Long>

    @Update
    suspend fun update(vararg expenses: IndividualExpenseEntity): Int

    @Delete
    suspend fun delete(vararg expense: IndividualExpenseEntity): Int

    @Query("DELETE FROM individual_expense")
    suspend fun deleteAll(): Int
}
