package com.purkt.mindexpense.expense.domain.model

import java.time.LocalDate

data class MonthlyExpenses(
    val expensesByDate: MutableMap<LocalDate, DailyExpenses>,
    val startDate: LocalDate,
    val endDate: LocalDate
)