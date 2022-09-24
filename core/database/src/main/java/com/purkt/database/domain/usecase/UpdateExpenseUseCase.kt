package com.purkt.database.domain.usecase

import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.repo.ExpenseRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Update the target expense data in the database.
     * @param expenses The target expense(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: Expense) = expenseRepository.updateExpense(*expenses)
}