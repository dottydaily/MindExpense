package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import com.purkt.model.domain.model.RecurringExpense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindAllRecurringExpensesUseCase @Inject constructor(
    private val repository: RecurringExpenseRepository
) {
    /**
     * Find all recurring expenses from database.
     * @return The [Flow] of list of all [RecurringExpense] from the database.
     */
    suspend operator fun invoke() = repository.findAllExpenses()
}
