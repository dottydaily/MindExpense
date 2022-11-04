package com.purkt.database.domain.usecase.individualexpense

import com.purkt.database.domain.repo.IndividualExpenseRepository
import com.purkt.model.domain.model.IndividualExpense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindAllIndividualExpensesUseCase @Inject constructor(
    private val repository: IndividualExpenseRepository
) {
    /**
     * Find all individual expenses from database.
     * @return The [Flow] of list of all [IndividualExpense] from the database.
     */
    suspend operator fun invoke() = repository.findAllExpenses()
}
