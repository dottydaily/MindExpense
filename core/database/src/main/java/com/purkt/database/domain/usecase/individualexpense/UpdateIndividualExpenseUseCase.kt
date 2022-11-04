package com.purkt.database.domain.usecase.individualexpense

import com.purkt.database.domain.repo.IndividualExpenseRepository
import com.purkt.model.domain.model.IndividualExpense
import javax.inject.Inject

class UpdateIndividualExpenseUseCase @Inject constructor(
    private val repository: IndividualExpenseRepository
) {
    /**
     * Update the target individual expense data in the database.
     * @param expenses The target individual expense(s) to be updated in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: IndividualExpense) = repository.updateExpense(*expenses)
}
