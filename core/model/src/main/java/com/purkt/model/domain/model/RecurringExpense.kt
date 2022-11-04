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
    val dayOfMonth: Int,
    val time: LocalTime
): Expense {
    fun mapToExpense(
        targetMonth: Month,
        targetYear: Int,
        targetTime: LocalTime
    ): IndividualExpense? {
        try {
            val targetLocalDateTime = with(targetTime) {
                LocalDateTime.of(targetYear, targetMonth, 1, hour, minute, second)
            }.with(TemporalAdjusters.lastDayOfMonth())

            return IndividualExpense(
                id = 0
            )
        } catch (e: Throwable) {
            Timber.e("Error when try to mapping RecurringExpense to Expense : ${e.message}")
            return null
        }
    }
}