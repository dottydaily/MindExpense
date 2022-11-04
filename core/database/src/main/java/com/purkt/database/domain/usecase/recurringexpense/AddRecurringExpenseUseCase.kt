package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import com.purkt.model.domain.model.RecurringExpense
import javax.inject.Inject

class AddRecurringExpenseUseCase @Inject constructor(
    private val repository: RecurringExpenseRepository
) {
    /**
     * Add new recurring expense data into database.
     * @param expenses The target recurring expense(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: RecurringExpense) = repository.addExpense(*expenses)
}
