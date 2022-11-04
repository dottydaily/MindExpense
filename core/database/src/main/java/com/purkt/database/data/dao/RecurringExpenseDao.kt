package com.purkt.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.purkt.database.data.entity.RecurringExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringExpenseDao {
    @Query("SELECT * FROM recurring_expense")
    fun findAll(): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expense WHERE id=:id")
    suspend fun findById(id: Int): RecurringExpenseEntity?

    @Query("SELECT COUNT(*) FROM recurring_expense")
    suspend fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg expenses: RecurringExpenseEntity): List<Long>

    @Update
    suspend fun update(vararg expenses: RecurringExpenseEntity): Int

    @Delete
    suspend fun delete(vararg expense: RecurringExpenseEntity): Int

    @Query("DELETE FROM recurring_expense")
    suspend fun deleteAll(): Int
}
