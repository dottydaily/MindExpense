package com.purkt.database.domain.usecase

import com.purkt.database.domain.repo.IndividualExpenseRepository
import javax.inject.Inject

class DeleteAllIndividualExpenseUseCase @Inject constructor(
    private val repository: IndividualExpenseRepository
) {
    /**
     * Delete all individual expense data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke() = repository.deleteAllExpense()
}
