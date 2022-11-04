package com.purkt.database.domain.utils

import com.purkt.database.domain.exception.DatabaseOperationFailedException
import timber.log.Timber

object DatabaseOperationHandler {
    /**
     * Do the operation and print log when there is a thrown exception from the operation block.
     * @param failedResult The default failed value to return after the exception is thrown.
     * @param operation The block of operation to do.
     * @return Return the result from the operation if it is succeeded. Otherwise, return [failedResult].
     */
    suspend fun<T> doDatabaseOperation(failedResult: T, operation: suspend () -> T): T {
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
