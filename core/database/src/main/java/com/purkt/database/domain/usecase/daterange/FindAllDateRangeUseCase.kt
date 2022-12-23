package com.purkt.database.domain.usecase.daterange

import com.purkt.database.domain.repo.DateRangeRepository
import com.purkt.model.domain.model.DateRange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindAllDateRangeUseCase @Inject constructor(
    private val repository: DateRangeRepository
) {
    /**
     * Find all date range from database.
     * @return The [Flow] of list of all [DateRange] from the database.
     */
    suspend operator fun invoke(): Flow<List<DateRange>> {
        return repository.findAllDateRanges()
    }
}
