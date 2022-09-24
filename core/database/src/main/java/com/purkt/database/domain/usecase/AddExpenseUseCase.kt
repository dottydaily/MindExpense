package com.purkt.database.domain.usecase

import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.repo.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Add new expense data into database.
     * @param expenses The target expense(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: Expense) = expenseRepository.addExpense(*expenses)
}