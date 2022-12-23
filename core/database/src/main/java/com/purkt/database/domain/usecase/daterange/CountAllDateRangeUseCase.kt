package com.purkt.database.domain.usecase.daterange

import com.purkt.database.domain.repo.DateRangeRepository
import javax.inject.Inject

class CountAllDateRangeUseCase @Inject constructor(
    private val repository: DateRangeRepository
) {
    /**
     * Find a total number of date range data in the database.
     * @return The total number of date range data.
     */
    suspend operator fun invoke(): Int {
        return repository.countAllDateRange()
    }
}
