package com.purkt.model.domain.model

import java.time.LocalDate

data class DailyExpenses(
    val expenses: MutableList<IndividualExpense>,
    val date: LocalDate
) {
    fun getTotalAmount(): Double {
        return expenses.sumOf { eachExpense -> eachExpense.amount }
    }
}
