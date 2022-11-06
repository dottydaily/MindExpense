package com.purkt.model.domain.model

import timber.log.Timber
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.temporal.TemporalAdjusters
import java.util.*

data class RecurringExpense(
    val id: Int = 0,
    override var title: String = "",
    override var description: String = "",
    override var amount: Double = 0.0,
    override var currency: Currency = Currency.getInstance(Locale.getDefault()),
    val dayOfMonth: Int
) : Expense {
    fun mapToIndividualExpense(
        id: Int,
        targetMonth: Month,
        targetYear: Int
    ): IndividualExpense? {
        return try {
            val targetLocalDateTime = try {
                LocalDateTime.of(targetYear, targetMonth, dayOfMonth, 0, 0, 0)
            } catch (e: DateTimeException) {
                Timber.e("Can't create new date time: ${e.message}")
                LocalDateTime.of(targetYear, targetMonth, 1, 0, 0, 0)
                    .with(TemporalAdjusters.lastDayOfMonth())
            }

            IndividualExpense(
                id = id,
                title = title,
                description = description,
                amount = amount,
                currency = currency,
                dateTime = targetLocalDateTime
            )
        } catch (e: Throwable) {
            Timber.e("Error when try to mapping RecurringExpense to Expense : ${e.message}")
            null
        }
    }
}
