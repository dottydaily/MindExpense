package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import javax.inject.Inject

class DeleteAllRecurringExpenseUseCase @Inject constructor(
    private val repository: RecurringExpenseRepository
) {
    /**
     * Delete all recurring expense data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke() = repository.deleteAllExpense()
}
