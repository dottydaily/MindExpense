package com.purkt.model.domain.model

import timber.log.Timber
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
        targetMonth: Month,
        targetYear: Int,
        targetTime: LocalTime
    ): IndividualExpense? {
        return try {
            val targetLocalDateTime = with(targetTime) {
                LocalDateTime.of(targetYear, targetMonth, 1, hour, minute, second)
            }.with(TemporalAdjusters.lastDayOfMonth())

            IndividualExpense(
                id = IndividualExpense.ID_FOR_RECURRING_EXPENSE,
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
