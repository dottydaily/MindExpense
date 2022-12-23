package com.purkt.database.domain.usecase.daterange

import com.purkt.database.domain.repo.DateRangeRepository
import com.purkt.model.domain.model.DateRange
import javax.inject.Inject

class DeleteDateRangeUseCase @Inject constructor(
    private val repository: DateRangeRepository
) {
    /**
     * Delete the target date range data in the database.
     * @param dateRanges The target date range(s) to be deleted in the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg dateRanges: DateRange): Boolean {
        return repository.deleteDateRange(*dateRanges)
    }
}
