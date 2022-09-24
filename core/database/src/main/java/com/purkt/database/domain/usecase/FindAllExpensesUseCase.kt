package com.purkt.database.domain.usecase

import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.repo.ExpenseRepository
import javax.inject.Inject

class FindAllExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Find all expenses from database.
     * @return The list of all [Expense] from the database.
     */
    suspend operator fun invoke() = expenseRepository.findAllExpenses()
}
