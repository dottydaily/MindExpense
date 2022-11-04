package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import com.purkt.model.domain.model.RecurringExpense
import javax.inject.Inject

class UpdateRecurringExpenseUseCase @Inject constructor(
    private val repository: RecurringExpenseRepository
) {
    /**
     * Update the target recurring expense data in the database.
     * @param expenses The target recurring expense(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: RecurringExpense) = repository.updateExpense(*expenses)
}
