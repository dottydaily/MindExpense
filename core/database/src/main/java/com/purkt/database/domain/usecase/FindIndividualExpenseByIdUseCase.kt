package com.purkt.database.domain.usecase

import com.purkt.database.domain.repo.IndividualExpenseRepository
import javax.inject.Inject

class FindIndividualExpenseByIdUseCase @Inject constructor(
    private val expenseRepository: IndividualExpenseRepository
) {
    /**
     * Find the target individual expense by using its ID.
     * @param id The ID of the the target individual expense.
     * @return Return the target individual expense if it is found in database. Otherwise, return null.
     */
    suspend operator fun invoke(id: Int) = expenseRepository.findExpenseById(id)
}
