package com.purkt.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.purkt.database.data.entity.DateRangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DateRangeDao {
    @Query("SELECT * FROM date_range")
    fun findAll(): Flow<List<DateRangeEntity>>

    @Query("SELECT * FROM date_range WHERE id=:id")
    suspend fun findById(id: Int): DateRangeEntity?

    @Query("SELECT COUNT(*) FROM date_range")
    suspend fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg dateRanges: DateRangeEntity): List<Long>

    @Update
    suspend fun update(vararg dateRanges: DateRangeEntity): Int

    @Delete
    suspend fun delete(vararg dateRanges: DateRangeEntity): Int

    @Query("DELETE FROM date_range")
    suspend fun deleteAll(): Int
}
