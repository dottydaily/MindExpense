package com.purkt.database.domain.repo

import com.purkt.model.domain.model.IndividualExpense
import kotlinx.coroutines.flow.Flow

interface IndividualExpenseRepository {
    /**
     * Find all individual expenses from database.
     * @return The [Flow] of list of all [IndividualExpense] from the database.
     */
    suspend fun findAllExpenses(): Flow<List<IndividualExpense>>

    /**
     * Find the target individual expense by using its ID.
     * @param id The ID of the the target individual expense.
     * @return Return the target individual expense if it is found in database. Otherwise, return null.
     */
    suspend fun findExpenseById(id: Int): IndividualExpense?

    /**
     * Find a total number of individual expense data in the database.
     * @return The total number of individual expense data.
     */
    suspend fun countAllExpense(): Int

    /**
     * Add new individual expense data into database.
     * @param expenses The target individual expense(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun addExpense(vararg expenses: IndividualExpense): Boolean

    /**
     * Update the target individual expense data in the database.
     * @param expenses The target individual expense(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun updateExpense(vararg expenses: IndividualExpense): Boolean

    /**
     * Delete the target individual expense data in the database.
     * @param expenses The target individual expense(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteExpense(vararg expenses: IndividualExpense): Boolean

    /**
     * Delete all individual expense data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteAllExpense(): Boolean
}
