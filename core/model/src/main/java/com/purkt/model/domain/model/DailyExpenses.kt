package com.purkt.model.domain.model

import java.time.LocalDate

data class DailyExpenses(
    val expenses: MutableList<Expense>,
    val date: LocalDate
) {
    fun getTotalAmount(): Double {
        return expenses.sumOf { eachExpense -> eachExpense.amount }
    }
}
