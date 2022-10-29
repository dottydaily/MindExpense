package com.purkt.model.domain.model

import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.temporal.TemporalAdjusters
import java.util.Currency

data class RecurringExpense(
    val dayOfMonth: Int,
    val time: LocalTime,
    val amount: Double,
    val currency: Currency
) {
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

            )
        } catch (e: Throwable) {
            Timber.e("Error when try to mapping RecurringExpense to Expense : ${e.message}")
            return null
        }
    }
}