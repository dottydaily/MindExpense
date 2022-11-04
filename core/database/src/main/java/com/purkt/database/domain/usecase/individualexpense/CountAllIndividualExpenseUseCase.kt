package com.purkt.database.domain.usecase.individualexpense

import com.purkt.database.domain.repo.IndividualExpenseRepository
import javax.inject.Inject

class CountAllIndividualExpenseUseCase @Inject constructor(
    private val repository: IndividualExpenseRepository
) {
    /**
     * Find a total number of individual expense data in the database.
     * @return The total number of individual expense data.
     */
    suspend operator fun invoke() = repository.countAllExpense()
}
