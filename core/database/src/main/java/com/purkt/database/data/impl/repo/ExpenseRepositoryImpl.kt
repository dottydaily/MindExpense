package com.purkt.database.data.impl.repo

import com.purkt.database.data.dao.ExpenseDao
import com.purkt.database.data.entity.ExpenseEntity
import com.purkt.database.domain.exception.DatabaseOperationFailedException
import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.repo.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.transform
import timber.log.Timber
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {
    override suspend fun findAllExpenses(): Flow<List<Expense>> {
        return doDatabaseOperation(failedResult = emptyFlow()) {
            val targetFlow = dao.findAll()
                .transform { entities ->
                    val targetList = entities.map {
                        ExpenseEntity.mapToDomainModel(it)
                    }
                    emit(targetList)
                }

            return@doDatabaseOperation targetFlow
        }
    }

    override suspend fun findExpenseById(id: Int): Expense? {
        return doDatabaseOperation(failedResult = null) {
            val target = dao.findById(id)
                ?: throw DatabaseOperationFailedException(
                    operation = "findExpenseById",
                    description = "Expense isn't found. (ID=$id)"
                )

            ExpenseEntity.mapToDomainModel(target)
        }
    }

    override suspend fun countAllExpense(): Int {
        return doDatabaseOperation(failedResult = 0) {
            dao.countAll()
        }
    }

    override suspend fun addExpense(vararg expenses: Expense): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val newEntities = expenses.map {
                ExpenseEntity.mapFromDomainModel(it)
            }.toTypedArray()

            val insertedIdList = dao.insert(*newEntities)
            if (insertedIdList.size != expenses.size) {
                throw DatabaseOperationFailedException(
                    operation = "addExpense",
                    description = "Failed to insert some target expense into the database."
                )
            }

            true
        }
    }

    override suspend fun updateExpense(vararg expenses: Expense): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val targetEntities = expenses.map {
                ExpenseEntity.mapFromDomainModel(it)
            }.toTypedArray()
            val totalUpdated = dao.update(*targetEntities)
            if (totalUpdated != expenses.size) {
                throw DatabaseOperationFailedException(
                    operation = "updateExpense",
                    description = "Failed to update some target expense in the database."
                )
            }

            true
        }
    }

    override suspend fun deleteExpense(vararg expenses: Expense): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val targetEntities = expenses.map {
                ExpenseEntity.mapFromDomainModel(it)
            }.toTypedArray()
            val totalDeleted = dao.delete(*targetEntities)
            if (totalDeleted != expenses.size) {
                throw DatabaseOperationFailedException(
                    operation = "deleteExpense",
                    description = "Failed to delete some target expense in the database."
                )
            }

            true
        }
    }

    override suspend fun deleteAllExpense(): Boolean {
        return doDatabaseOperation(failedResult = false) {
            val totalCountBeforeDelete = countAllExpense()
            val totalDeleted = dao.deleteAll()
            val totalCountAfterDelete = countAllExpense()

            if (totalCountBeforeDelete != totalDeleted || totalCountAfterDelete != 0) {
                throw DatabaseOperationFailedException(
                    operation = "deleteAllExpense",
                    description = "Failed to delete all expense in the database."
                )
            }

            true
        }
    }

    /**
     * Do the operation and print log when there is a thrown exception from the operation block.
     * @param failedResult The default failed value to return after the exception is thrown.
     * @param operation The block of operation to do.
     * @return Return the result from the operation if it is succeeded. Otherwise, return [failedResult].
     */
    private suspend fun<T> doDatabaseOperation(failedResult: T, operation: suspend () -> T): T {
        return try {
            operation.invoke()
        } catch (e: DatabaseOperationFailedException) {
            Timber.e(e.message)
            failedResult
        } catch (e: Throwable) {
            Timber.e("[DB] Unexpected error : ${e.message}")
            failedResult
        }
    }
}
