package com.purkt.database.domain.repo

import com.purkt.model.domain.model.RecurringExpense
import kotlinx.coroutines.flow.Flow

interface RecurringExpenseRepository {
    /**
     * Find all recurring expenses from database.
     * @return The [Flow] of list of all [RecurringExpense] from the database.
     */
    suspend fun findAllExpenses(): Flow<List<RecurringExpense>>

    /**
     * Find the target recurring expense by using its ID.
     * @param id The ID of the the target recurring expense.
     * @return Return the target recurring expense if it is found in database. Otherwise, return null.
     */
    suspend fun findExpenseById(id: Int): RecurringExpense?

    /**
     * Find a total number of recurring expense data in the database.
     * @return The total number of recurring expense data.
     */
    suspend fun countAllExpense(): Int

    /**
     * Add new recurring expense data into database.
     * @param expenses The target recurring expense(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun addExpense(vararg expenses: RecurringExpense): Boolean

    /**
     * Update the target recurring expense data in the database.
     * @param expenses The target recurring expense(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun updateExpense(vararg expenses: RecurringExpense): Boolean

    /**
     * Delete the target recurring expense data in the database.
     * @param expenses The target recurring expense(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteExpense(vararg expenses: RecurringExpense): Boolean

    /**
     * Delete all recurring expense data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteAllExpense(): Boolean
}
