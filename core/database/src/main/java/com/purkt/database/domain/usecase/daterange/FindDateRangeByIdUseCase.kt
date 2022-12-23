package com.purkt.database.domain.usecase.daterange

import com.purkt.database.domain.repo.DateRangeRepository
import com.purkt.model.domain.model.DateRange
import javax.inject.Inject

class FindDateRangeByIdUseCase @Inject constructor(
    private val repository: DateRangeRepository
) {
    /**
     * Find the target date range by using its ID.
     * @param id The ID of the the target date range.
     * @return Return the target date range if it is found in database. Otherwise, return null.
     */
    suspend operator fun invoke(id: Int): DateRange? {
        return repository.findDateRangeById(id)
    }
}