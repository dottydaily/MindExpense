package com.purkt.database.domain.repo

import com.purkt.model.domain.model.DateRange
import kotlinx.coroutines.flow.Flow

interface DateRangeRepository {
    /**
     * Find all date range from database.
     * @return The [Flow] of list of all [DateRange] from the database.
     */
    suspend fun findAllDateRanges(): Flow<List<DateRange>>

    /**
     * Find the target date range by using its ID.
     * @param id The ID of the the target date range.
     * @return Return the target date range if it is found in database. Otherwise, return null.
     */
    suspend fun findDateRangeById(id: Int): DateRange?

    /**
     * Find a total number of date range data in the database.
     * @return The total number of date range data.
     */
    suspend fun countAllDateRange(): Int

    /**
     * Add new date range data into database.
     * @param dateRanges The target date range(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun addDateRange(vararg dateRanges: DateRange): Boolean

    /**
     * Update the target date range data in the database.
     * @param dateRanges The target date range(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun updateDateRange(vararg dateRanges: DateRange): Boolean

    /**
     * Delete the target date range data in the database.
     * @param dateRanges The target date range(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteDateRange(vararg dateRanges: DateRange): Boolean

    /**
     * Delete all date range data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteAllDateRange(): Boolean
}
