package com.purkt.mindexpense.expense.domain.model

import com.purkt.database.domain.model.Expense
import java.time.LocalDate

data class DailyExpenses(
    val expenses: MutableList<Expense>,
    val date: LocalDate
) {
    fun getTotalAmount(): Double {
        return expenses.sumOf { eachExpense -> eachExpense.amount }
    }
}