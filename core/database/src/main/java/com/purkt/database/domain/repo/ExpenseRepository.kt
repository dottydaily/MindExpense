package com.purkt.database.domain.repo

import com.purkt.database.domain.model.Expense

interface ExpenseRepository {
    /**
     * Find all expenses from database.
     * @return The list of all [Expense] from the database.
     */
    suspend fun findAllExpenses(): List<Expense>

    /**
     * Find the target expense by using its ID.
     * @param id The ID of the the target expense.
     * @return Return the target expense if it is found in database. Otherwise, return null.
     */
    suspend fun findExpenseById(id: Int): Expense?

    /**
     * Find a total number of expense data in the database.
     * @return The total number of expense data.
     */
    suspend fun countAllExpense(): Int

    /**
     * Add new expense data into database.
     * @param expenses The target expense(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun addExpense(vararg expenses: Expense): Boolean

    /**
     * Update the target expense data in the database.
     * @param expenses The target expense(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun updateExpense(vararg expenses: Expense): Boolean

    /**
     * Delete the target expense data in the database.
     * @param expenses The target expense(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteExpense(vararg expenses: Expense): Boolean

    /**
     * Delete all expense data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend fun deleteAllExpense(): Boolean
}
