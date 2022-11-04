package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import com.purkt.model.domain.model.RecurringExpense
import javax.inject.Inject

class DeleteRecurringExpenseUseCase @Inject constructor(
    private val repository: RecurringExpenseRepository
) {
    /**
     * Delete the target recurring expense data in the database.
     * @param expenses The target recurring expense(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: RecurringExpense) = repository.deleteExpense(*expenses)
}
