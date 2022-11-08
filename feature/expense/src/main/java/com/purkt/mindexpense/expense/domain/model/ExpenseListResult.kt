package com.purkt.mindexpense.expense.domain.model

import com.purkt.model.domain.model.ExpenseSummary

sealed class ExpenseListResult {
    object Loading : ExpenseListResult()
    class ResultWithData(val summary: ExpenseSummary) : ExpenseListResult()
    object EmptyResult : ExpenseListResult()
}
