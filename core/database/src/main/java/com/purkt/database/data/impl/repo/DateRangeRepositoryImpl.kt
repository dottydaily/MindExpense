package com.purkt.database.data.impl.repo

import com.purkt.database.data.dao.DateRangeDao
import com.purkt.database.data.entity.DateRangeEntity
import com.purkt.database.domain.exception.DatabaseOperationFailedException
import com.purkt.database.domain.repo.DateRangeRepository
import com.purkt.database.domain.utils.DatabaseOperationHandler.doDatabaseOperation
import com.purkt.model.domain.model.DateRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class DateRangeRepositoryImpl @Inject constructor(
    private val dao: DateRangeDao
) : DateRangeRepository {
    override suspend fun findAllDateRanges(): Flow<List<DateRange>> {
        return doDatabaseOperation(failedResult = emptyFlow()) {
            val targetFlow = dao.findAll()
                .transform { entities ->
                    val targetList = entities.map { DateRangeEntity.mapToDomainModel(it) }
                    emit(targetList)
                }

            return@doDatabaseOperation targetFlow
        }
    }

    override suspend fun findDateRangeById(id: Int): DateRange? {
        return doDatabaseOperation(failedResult = null) {
            val target = dao.findById(id)
                ?: throw DatabaseOperationFailedException(
                    operation = "findDateRangeById",
                    description = "DateRange isn't found (id=$id)"
                )
            DateRangeEntity.mapToDomainModel(target)
        }
    }

    override suspend fun countAllDateRange(): Int {
        return doDatabaseOperation(failedResult = 0) {
            dao.countAll()
        }
    }

    override suspend fun addDateRange(vararg dateRanges: DateRange): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val entities = dateRanges.map {
                DateRangeEntity.mapFromDomainModel(it)
            }.toTypedArray()

            val insertedIdList = dao.insert(*entities)
            if (insertedIdList.size != dateRanges.size) {
                throw DatabaseOperationFailedException(
                    operation = "addDateRange",
                    description = "Failed to add some date range into the database."
                )
            }

            true
        }
    }

    override suspend fun updateDateRange(vararg dateRanges: DateRange): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val entities = dateRanges.map {
                DateRangeEntity.mapFromDomainModel(it)
            }.toTypedArray()

            val updatedCount = dao.update(*entities)
            if (updatedCount != dateRanges.size) {
                throw DatabaseOperationFailedException(
                    operation = "updateDateRange",
                    description = "Failed to update some date range in the database."
                )
            }

            true
        }
    }

    override suspend fun deleteDateRange(vararg dateRanges: DateRange): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val entities = dateRanges.map {
                DateRangeEntity.mapFromDomainModel(it)
            }.toTypedArray()

            val deleteCount = dao.delete(*entities)
            if (deleteCount != dateRanges.size) {
                throw DatabaseOperationFailedException(
                    operation = "deleteDateRange",
                    description = "Failed to delete some date range from the database."
                )
            }

            true
        }
    }

    override suspend fun deleteAllDateRange(): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val totalCountBeforeDelete = countAllDateRange()
            val deleteCount = dao.deleteAll()
            val totalCountAfterDelete = countAllDateRange()

            if (totalCountBeforeDelete != deleteCount || totalCountAfterDelete != 0) {
                throw DatabaseOperationFailedException(
                    operation = "deleteAllDateRange",
                    description = "Failed to delete all date range from the database."
                )
            }

            true
        }
    }
}