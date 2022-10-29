package com.purkt.database.domain.usecase

import com.purkt.database.domain.repo.ExpenseRepository
import com.purkt.model.domain.model.Expense
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Delete the target expense data in the database.
     * @param expenses The target expense(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: Expense) = expenseRepository.deleteExpense(*expenses)
}
