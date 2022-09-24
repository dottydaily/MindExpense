package com.purkt.database.domain.usecase

import com.purkt.database.domain.repo.ExpenseRepository
import javax.inject.Inject

class FindExpenseByIdUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Find the target expense by using its ID.
     * @param id The ID of the the target expense.
     * @return Return the target expense if it is found in database. Otherwise, return null.
     */
    suspend operator fun invoke(id: Int) = expenseRepository.findExpenseById(id)
}