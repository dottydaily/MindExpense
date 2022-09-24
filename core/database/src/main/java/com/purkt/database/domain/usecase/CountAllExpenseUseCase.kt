package com.purkt.database.domain.usecase

import com.purkt.database.domain.repo.ExpenseRepository
import javax.inject.Inject

class CountAllExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Find a total number of expense data in the database.
     * @return The total number of expense data.
     */
    suspend operator fun invoke() = expenseRepository.countAllExpense()
}
