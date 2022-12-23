package com.purkt.database.domain.usecase.daterange

import com.purkt.database.domain.repo.DateRangeRepository
import javax.inject.Inject

class DeleteAllDateRangeUseCase @Inject constructor(
    private val repository: DateRangeRepository
) {
    /**
     * Delete all date range data in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(): Boolean {
        return repository.deleteAllDateRange()
    }
}
