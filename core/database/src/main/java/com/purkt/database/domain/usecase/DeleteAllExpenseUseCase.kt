package com.purkt.database.domain.usecase

import com.purkt.database.domain.repo.ExpenseRepository
import javax.inject.Inject

class DeleteAllExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Delete all expense data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke() = expenseRepository.deleteAllExpense()
}
