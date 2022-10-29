package com.purkt.model.domain.model

import java.time.LocalDate

data class ExpenseSummary(
    val expensesByDate: MutableMap<LocalDate, DailyExpenses>,
    val startDate: LocalDate,
    val endDate: LocalDate
)
