package com.purkt.database.domain.usecase.recurringexpense

import com.purkt.database.domain.repo.RecurringExpenseRepository
import javax.inject.Inject

class FindRecurringExpenseByIdUseCase @Inject constructor(
    private val expenseRepository: RecurringExpenseRepository
) {
    /**
     * Find the target recurring expense by using its ID.
     * @param id The ID of the the target recurring expense.
     * @return Return the target recurring expense if it is found in database. Otherwise, return null.
     */
    suspend operator fun invoke(id: Int) = expenseRepository.findExpenseById(id)
}
