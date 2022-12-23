package com.purkt.database.domain.usecase.daterange

import com.purkt.database.domain.repo.DateRangeRepository
import com.purkt.model.domain.model.DateRange
import javax.inject.Inject

class AddDateRangeUseCase @Inject constructor(
    private val repository: DateRangeRepository
) {
    /**
     * Add new date range data into database.
     * @param dateRanges The target date range(s) to be inserted into the database.
     * @return Return true if the operation is succeeded. Otherwise, return false.
     */
    suspend operator fun invoke(vararg dateRanges: DateRange): Boolean {
        return repository.addDateRange(*dateRanges)
    }
}
