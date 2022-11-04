package com.purkt.database.domain.usecase.individualexpense

import com.purkt.database.domain.repo.IndividualExpenseRepository
import com.purkt.model.domain.model.IndividualExpense
import javax.inject.Inject

class AddIndividualExpenseUseCase @Inject constructor(
    private val repository: IndividualExpenseRepository
) {
    /**
     * Add new individual expense data into database.
     * @param expenses The target individual expense(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg expenses: IndividualExpense) = repository.addExpense(*expenses)
}
