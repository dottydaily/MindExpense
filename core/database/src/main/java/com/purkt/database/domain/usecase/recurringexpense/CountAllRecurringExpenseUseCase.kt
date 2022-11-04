package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import javax.inject.Inject

class CountAllRecurringExpenseUseCase @Inject constructor(
    private val repository: RecurringExpenseRepository
) {
    /**
     * Find a total number of recurring expense data in the database.
     * @return The total number of individual expense data.
     */
    suspend operator fun invoke() = repository.countAllExpense()
}
